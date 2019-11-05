package qunar.tc.bistoury.commands.download;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.job.ContinueResponseJob;
import qunar.tc.bistoury.remoting.command.DownloadCommand;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author leix.xie
 * @date 2019/11/5 15:44
 * @describe
 */
public class DownloadFileTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(DownloadFileTask.class);

    private final String id;
    private final long maxRunningMs;
    private final DownloadCommand command;
    private final ResponseHandler handler;
    private final SettableFuture<Integer> future = SettableFuture.create();

    public DownloadFileTask(String id, long maxRunningMs, DownloadCommand command, ResponseHandler handler) {
        this.id = id;
        this.maxRunningMs = maxRunningMs;
        this.command = command;
        this.handler = handler;
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
        private byte[] bytes = new byte[4 * 1024];

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
            inputStream = new FileInputStream(file);
        }

        @Override
        public boolean doResponse() throws Exception {
            int read = inputStream.read(bytes);
            if (read == -1) {
                return true;
            }
            if (read > 0) {
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
        public void finish() throws Exception {
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
