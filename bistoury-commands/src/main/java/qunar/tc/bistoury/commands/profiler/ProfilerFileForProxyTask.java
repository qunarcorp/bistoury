package qunar.tc.bistoury.commands.profiler;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static qunar.tc.bistoury.remoting.protocol.CommandCode.*;

/**
 * @author cai.wen created on 19-12-11 下午2:22
 */
//todo 和下载文件分支结合
public class ProfilerFileForProxyTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(ProfilerFileForProxyTask.class);

    private static final ListeningExecutorService agentExecutor = AgentRemotingExecutor.getExecutor();

    private final String id;

    private final long maxRunningMs;

    private final ResponseHandler handler;

    private final String profilerId;

    private volatile ListenableFuture<Integer> future;

    private static final int PROFILER_DIR_SIZE = 128;
    private static final int PROFILER_NAME_SIZE = 128;
    private static final int PROFILER_HEADER_SIZE = 256;
    private static final int PROFILER_BUFFER_SIZE = 8192;

    ProfilerFileForProxyTask(String id, long maxRunningMs, ResponseHandler handler, String profilerId) {
        this.id = id;
        this.maxRunningMs = maxRunningMs;
        this.handler = handler;
        this.profilerId = profilerId;
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
    public ListenableFuture<Integer> execute() {
        future = agentExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                final Optional<File> profilerDirRef = ProfilerUtil.getProfilerDir(BistouryStore.PROFILER_ROOT_PATH, profilerId);
                if (!profilerDirRef.isPresent()) {
                    doWriteFileReadError(handler, profilerId + " not exists.");
                    logger.info("download profiler: {} not exists.", profilerId);
                    return -1;
                }
                String[] profilerFiles = profilerDirRef.get().list();
                if (profilerFiles != null) {
                    for (String profilerFile : profilerFiles) {
                        writeFileToProxy(new File(profilerDirRef.get().getAbsolutePath(), profilerFile), handler);
                    }
                }
                doWriteAllEndStateToProxy(profilerDirRef.get(), handler);
                return 0;
            }
        });
        return future;
    }

    @Override
    public void cancel() {
        try {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
        } catch (Exception e) {
            logger.error("cancel profiler file task error", e);
        }
    }

    private void writeFileToProxy(final File downloadFile, final ResponseHandler handler) {
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(downloadFile))) {
            byte[] buf = new byte[PROFILER_BUFFER_SIZE];
            fillFileInfoChunk(downloadFile, buf);
            int read;
            do {
                read = input.read(buf, PROFILER_HEADER_SIZE, buf.length - PROFILER_HEADER_SIZE);
                if (read != -1) {
                    if (read < (PROFILER_BUFFER_SIZE - PROFILER_HEADER_SIZE)) {
                        buf = Arrays.copyOf(buf, PROFILER_HEADER_SIZE + read);
                    }
                    doWriteFileToProxy(handler, buf);
                } else {
                    break;
                }
            } while (true);
            doWriteEndStateToProxy(downloadFile, handler);
        } catch (Exception e) {
            logger.error("download file for proxy error.", e);
            doWriteFileReadError(handler, e.getMessage());
        }
    }

    private byte[] fillFileInfoChunk(File downloadFile, byte[] sourceBuf) {
        return fillFileInfoChunk(downloadFile.getParentFile().getName(), downloadFile.getName(), sourceBuf);
    }

    private byte[] fillFileInfoChunk(String profilerIdDir, String name, byte[] sourceBuf) {
        byte[] profilerDirBytes = profilerIdDir.getBytes(Charsets.UTF_8);
        byte[] nameBytes = name.getBytes(Charsets.UTF_8);
        System.arraycopy(profilerDirBytes, 0, sourceBuf, 0, Math.min(profilerDirBytes.length, PROFILER_DIR_SIZE));
        System.arraycopy(nameBytes, 0, sourceBuf, PROFILER_DIR_SIZE, Math.min(nameBytes.length, PROFILER_NAME_SIZE));
        return sourceBuf;
    }

    private void doWriteEndStateToProxy(File downloadFile, ResponseHandler handler) {
        handler.handle(REQ_TYPE_PROFILER_FILE_END.getCode(), fillFileInfoChunk(downloadFile, new byte[PROFILER_HEADER_SIZE]));
    }

    private void doWriteAllEndStateToProxy(File downloadDir, ResponseHandler handler) {
        handler.handle(REQ_TYPE_PROFILER_ALL_FILE_END.getCode(), fillFileInfoChunk(downloadDir.getName(), "", new byte[PROFILER_HEADER_SIZE]));
    }

    private void doWriteFileToProxy(ResponseHandler handler, byte[] chunk) {
        handler.handle(REQ_TYPE_PROFILER_FILE.getCode(), chunk);
    }

    private void doWriteFileReadError(ResponseHandler handler, String detailMsg) {
        handler.handle(REQ_TYPE_PROFILER_FILE_ERROR.getCode(), detailMsg);
    }
}
