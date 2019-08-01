package qunar.tc.bistoury.remoting.netty;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executors;

/**
 * @author zhenyu.nie created on 2018 2018/10/9 15:04
 */
public class AgentRemotingExecutor {

    private static final ListeningExecutorService executorService;

    static {
        int threadNum = Integer.parseInt(System.getProperty("bistoury.agent.thread.num", "16"));
        executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threadNum, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("log-agent-exec-%d").build()));
    }

    public static ListeningExecutorService getExecutor() {
        return executorService;
    }
}
