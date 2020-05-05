package qunar.tc.bistoury.commands.profiler;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.job.ContinueResponseJob;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static qunar.tc.bistoury.common.BistouryConstants.PROFILER_DIR_HEADER;
import static qunar.tc.bistoury.common.BistouryConstants.PROFILER_NAME_HEADER;
import static qunar.tc.bistoury.remoting.protocol.CommandCode.REQ_TYPE_PROFILER_ALL_FILE_END;
import static qunar.tc.bistoury.remoting.protocol.CommandCode.REQ_TYPE_PROFILER_FILE;
import static qunar.tc.bistoury.remoting.protocol.CommandCode.REQ_TYPE_PROFILER_FILE_END;
import static qunar.tc.bistoury.remoting.protocol.CommandCode.REQ_TYPE_PROFILER_FILE_ERROR;

/**
 * @author cai.wen created on 19-12-11 下午2:22
 */
public class ProfilerFileForProxyTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(ProfilerFileForProxyTask.class);

    private final String id;

    private final long maxRunningMs;

    private final ResponseHandler handler;

    private final String profilerId;

    private final SettableFuture<Integer> future = SettableFuture.create();

    private static final byte[] empty = new byte[0];
    private static final int PROFILER_BUFFER_SIZE = 8192;
    private static final int DEFAULT_DOWNLOAD_LIMIT_KB = 10 * 1024;
    private static final MetaStore META_STORE = MetaStores.getMetaStore();
    private final RateLimiter rateLimiter;

    ProfilerFileForProxyTask(String id, long maxRunningMs, ResponseHandler handler, String profilerId) {
        this.id = id;
        this.maxRunningMs = maxRunningMs;
        this.handler = handler;
        this.profilerId = profilerId;
        rateLimiter = RateLimiter.create(META_STORE.getIntProperty("download.kb.per.second", DEFAULT_DOWNLOAD_LIMIT_KB) * 1024);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getMaxRunningMs() {
        return maxRunningMs;
    }

    @Override
    public ListenableFuture<Integer> getResultFuture() {
        return future;
    }

    @Override
    public ContinueResponseJob createJob() {
        return new Job(AgentRemotingExecutor.getExecutor());
    }

    private class Job implements ContinueResponseJob {

        private final ListeningExecutorService executor;
        private InputStream currentInputStream;
        private File currentProfilerFile;
        private File profilerDir;
        private boolean fileWriteEnd = false;
        private List<File> profilerFiles;
        private int writingFileIndex = 0;

        private Job(ListeningExecutorService executor) {
            this.executor = executor;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void init() {
            final Optional<File> profilerDirRef = ProfilerUtil.getProfilerDir(BistouryStore.DEFAULT_PROFILER_ROOT_PATH, profilerId);
            if (!profilerDirRef.isPresent()) {
                logger.warn("download profiler: {} not exists.", profilerId);
                doWriteFileReadError(handler, profilerId + " not exists.");
                throw new RuntimeException("profiler file not exists. id: " + profilerId);
            }
            profilerDir = profilerDirRef.get();
            File[] needWriteFiles = profilerDir.listFiles();
            Objects.requireNonNull(needWriteFiles, "profiler files is null.");
            profilerFiles = ImmutableList.copyOf(needWriteFiles);
        }

        @Override
        public boolean doResponse() throws IOException {
            Optional<Integer> writeSizeRef = writeNextChunk();
            if (!writeSizeRef.isPresent()) {
                return true;
            }
            rateLimiter.acquire(writeSizeRef.get() == 0 ? 1 : writeSizeRef.get());
            return false;
        }

        private Optional<Integer> writeNextChunk() throws IOException {
            final byte[] buffer = new byte[PROFILER_BUFFER_SIZE];
            if (currentInputStream == null || fileWriteEnd) {
                fileWriteEnd = false;
                if (writingFileIndex >= profilerFiles.size()) {
                    return Optional.absent();
                }
                switchInputStream(profilerFiles.get(writingFileIndex));
            }
            int read;
            read = currentInputStream.read(buffer);
            if (read != -1) {
                if (read < (PROFILER_BUFFER_SIZE)) {
                    final byte[] endContent = Arrays.copyOf(buffer, read);
                    doWriteFileToProxy(currentProfilerFile, endContent);
                } else {
                    doWriteFileToProxy(currentProfilerFile, buffer);
                }
                return Optional.of(PROFILER_BUFFER_SIZE);
            }

            fileWriteEnd = true;
            closeStream(currentInputStream);
            writingFileIndex++;
            return Optional.of(0);
        }

        private void switchInputStream(File downloadFile) {
            currentProfilerFile = downloadFile;
            try {
                currentInputStream = new BufferedInputStream(new FileInputStream(downloadFile));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("profiler file not find.", e);
            }
        }

        private void closeStream(InputStream inputStream) throws IOException {
            if (inputStream != null) {
                inputStream.close();
                doWriteEndStateToProxy(currentProfilerFile);
            }
        }

        private void doWriteEndStateToProxy(File downloadFile) {
            Map<String, String> responseHeader = getFileInfoHeader(downloadFile);
            handler.handle(REQ_TYPE_PROFILER_FILE_END.getCode(), empty, responseHeader);
        }

        private void doWriteAllEndStateToProxy(File downloadDir) {
            Map<String, String> responseHeader = getFileInfoHeader(downloadDir.getName(), "");
            handler.handle(REQ_TYPE_PROFILER_ALL_FILE_END.getCode(), empty, responseHeader);
        }

        private void doWriteFileToProxy(File downloadFile, byte[] chunk) {
            Map<String, String> responseHeader = getFileInfoHeader(downloadFile);
            handler.handle(REQ_TYPE_PROFILER_FILE.getCode(), chunk, responseHeader);
        }

        private void doWriteFileReadError(ResponseHandler handler, String detailMsg) {
            handler.handle(REQ_TYPE_PROFILER_FILE_ERROR.getCode(), detailMsg.getBytes(Charsets.UTF_8), ImmutableMap.<String, String>of());
        }

        private Map<String, String> getFileInfoHeader(File downloadFile) {
            return getFileInfoHeader(downloadFile.getParentFile().getName(), downloadFile.getName());
        }

        private Map<String, String> getFileInfoHeader(String profilerIdDir, String name) {
            return ImmutableMap.of(PROFILER_DIR_HEADER, profilerIdDir, PROFILER_NAME_HEADER, name);
        }

        @Override
        public void clear() {
            if (currentInputStream != null) {
                try {
                    currentInputStream.close();
                } catch (IOException e) {
                    logger.warn("close profiler file inputStream error. id: " + id, e);
                }
            }
        }

        @Override
        public void finish() {
            doWriteAllEndStateToProxy(profilerDir);
            future.set(0);
        }

        @Override
        public void error(Throwable t) {
            future.setException(t);
        }

        @Override
        public void cancel() {
            future.cancel(true);
        }

        @Override
        public ListeningExecutorService getExecutor() {
            return executor;
        }
    }
}
