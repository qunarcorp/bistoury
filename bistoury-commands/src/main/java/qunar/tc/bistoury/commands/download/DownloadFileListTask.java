package qunar.tc.bistoury.commands.download;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.job.BytesJob;
import qunar.tc.bistoury.agent.common.job.ContinueResponseJob;
import qunar.tc.bistoury.common.CodeProcessResponse;
import qunar.tc.bistoury.common.FileUtil;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.common.TypeResponse;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.io.File;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019/11/4 16:32
 * @describe
 */
public class DownloadFileListTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(DownloadFileListTask.class);

    private final String id;

    private final ResponseHandler handler;

    private final long maxRunningMs;

    private final List<String> filePath;

    private final SettableFuture<Integer> future = SettableFuture.create();

    private static final Splitter FILE_PATH_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    public DownloadFileListTask(String id, String command, ResponseHandler handler, long maxRunningMs) {
        this.id = id;
        this.handler = handler;
        this.maxRunningMs = maxRunningMs;
        filePath = FILE_PATH_SPLITTER.splitToList(Strings.nullToEmpty(command).trim());
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
        return new Job();
    }

    @Override
    public ListenableFuture<Integer> getResultFuture() {
        return future;
    }

    private class Job extends BytesJob {

        protected Job() {
            super(id, handler, future);
        }

        @Override
        protected byte[] getBytes() throws Exception {
            TypeResponse<List<DownloadFileBean>> typeResponse = new TypeResponse<>();
            CodeProcessResponse<List<DownloadFileBean>> response = new CodeProcessResponse<>();
            typeResponse.setData(response);
            typeResponse.setType("downloadfilelist");

            List<DownloadFileBean> result = Lists.newArrayList();

            if (filePath != null && !filePath.isEmpty()) {
                listFile(result);
            }
            response.setCode(0);
            response.setData(result);
            return JacksonSerializer.serializeToBytes(typeResponse);
        }


        private void listFile(List<DownloadFileBean> result) {
            for (String path : filePath) {
                List<File> files = doListFile(path);
                List<DownloadFileBean> fileBeans = Lists.transform(files, new Function<File, DownloadFileBean>() {
                    @Override
                    public DownloadFileBean apply(File file) {
                        return new DownloadFileBean(file.getName(), file.getAbsolutePath(), file.length(), file.lastModified());
                    }
                });
                result.addAll(fileBeans);
            }
        }

        private List<File> doListFile(final String path) {
            return FileUtil.listFile(new File(path), Predicates.<File>alwaysTrue());
        }

        @Override
        public ListeningExecutorService getExecutor() {
            return AgentRemotingExecutor.getExecutor();
        }
    }
}
