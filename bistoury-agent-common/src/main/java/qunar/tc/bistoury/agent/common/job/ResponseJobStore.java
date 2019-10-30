package qunar.tc.bistoury.agent.common.job;

/**
 * @author zhenyu.nie created on 2019 2019/10/16 17:57
 */
public interface ResponseJobStore {

    void setWritable(boolean writable);

    void submit(ContinueResponseJob job);

    void pause(String id);

    void resume(String id);

    void stop(String id);

    void close();
}
