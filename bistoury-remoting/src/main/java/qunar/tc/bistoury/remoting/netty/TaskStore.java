package qunar.tc.bistoury.remoting.netty;

/**
 * @author zhenyu.nie created on 2019 2019/5/28 16:16
 */
public interface TaskStore {

    boolean register(Task task);

    void finish(String id);

    void cancel(String id);

    void close();
}
