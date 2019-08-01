package qunar.tc.bistoury.remoting.netty;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author zhenyu.nie created on 2018 2018/10/9 12:11
 */
public interface Task {

    String getId();

    long getMaxRunningMs();

    ListenableFuture<Integer> execute();

    void cancel();
}
