package qunar.tc.bistoury.agent.common.job;

import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * @author zhenyu.nie created on 2019 2019/10/16 19:20
 */
public interface ContinueResponseJob {

    String getId();

    void init() throws Exception;

    boolean doResponse() throws Exception;

    void clear();

    void finish() throws Exception;

    void error(Throwable t);

    void cancel();

    ListeningExecutorService getExecutor();
}
