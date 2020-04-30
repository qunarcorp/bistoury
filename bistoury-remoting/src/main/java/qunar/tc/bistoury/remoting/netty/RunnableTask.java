package qunar.tc.bistoury.remoting.netty;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author zhenyu.nie created on 2019 2019/10/30 15:49
 */
public interface RunnableTask {

    String getId();

    long getMaxRunningMs();

    ListenableFuture<Integer> execute();

    void pause();

    void resume();

    void cancel();
}
