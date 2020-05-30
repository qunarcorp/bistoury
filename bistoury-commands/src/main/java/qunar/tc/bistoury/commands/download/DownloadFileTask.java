package qunar.tc.bistoury.commands.download;

import com.google.common.base.Splitter;
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
import qunar.tc.bistoury.remoting.command.DownloadCommand;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019/11/5 15:44
 * @describe
 */
public class DownloadFileTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(DownloadFileTask.class);

    private static final Splitter FILE_PATH_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    private static final MetaStore META_STORE = MetaStores.getMetaStore();

    private static final int BYTE_KB = 4;

    private static final int DEFAULT_DOWNLOAD_LIMIT_KB = 10 * 1024;

    private final String id;
    private final long maxRunningMs;
    private final DownloadCommand command;
    private final ResponseHandler handler;
    private final SettableFuture<Integer> future = SettableFuture.create();
    private final RateLimiter rateLimiter;

    public DownloadFileTask(String id, long maxRunningMs, DownloadCommand command, ResponseHandler handler) {
        this.id = id;
        this.maxRunningMs = maxRunningMs;
        this.command = command;
        this.handler = handler;
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
    public ContinueResponseJob createJob() {
        return new Job(AgentRemotingExecutor.getExecutor());
    }

    @Override
    public ListenableFuture<Integer> getResultFuture() {
        return future;
    }

    private class Job implements ContinueResponseJob {
        private final ListeningExecutorService executor;
        private InputStream inputStream;
        private byte[] bytes = new byte[BYTE_KB * 1024];

        private Job(ListeningExecutorService executor) {
            this.executor = executor;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void init() throws Exception {
            File file = new File(command.getPath());
            ensureFileExists(file);
            if (!ensureFilePermission(file, command.getDir())) {
                throw new RuntimeException("download: " + file.getAbsolutePath() + ": No permission to download");
            }
            inputStream = new FileInputStream(file);
        }

        @Override
        public boolean doResponse() throws Exception {
            int read = inputStream.read(bytes);
            if (read == -1) {
                return true;
            }

            if (read > 0) {
                rateLimiter.acquire(read);
                handler.handle(Arrays.copyOfRange(bytes, 0, read));
            }
            return false;
        }

        private void ensureFileExists(File file) {
            if (!file.exists()) {
                throw new RuntimeException("download: " + file.getAbsolutePath() + ": No such file or directory");
            } else if (file.isDirectory()) {
                throw new RuntimeException("download: " + file.getAbsolutePath() + ": Is a directory");
            }
        }

        //确保要下载的文件在指定的目录下，不在指定目录下不能下载
        private boolean ensureFilePermission(File file, final String baseDirs) {
            List<String> paths = FILE_PATH_SPLITTER.splitToList(baseDirs);

            for (String path : paths) {
                if (doEnsureFilePermission(file, path)) {
                    return true;
                }
            }
            return false;
        }

        private boolean doEnsureFilePermission(File file, String basePath) {
            try {
                File baseFile = new File(basePath);
                if (!baseFile.exists() || !baseFile.isDirectory() || !file.exists()) {
                    return false;
                }

                if (file.getCanonicalPath().startsWith(baseFile.getCanonicalPath() + File.separator)) {
                    return true;
                }
            } catch (Exception e) {
                logger.error("ensure file permission error", e);
                return false;
            }
            return false;
        }

        @Override
        public void clear() {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }

        @Override
        public void finish() {
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
