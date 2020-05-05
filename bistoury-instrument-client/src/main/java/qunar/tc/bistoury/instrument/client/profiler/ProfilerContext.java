package qunar.tc.bistoury.instrument.client.profiler;

/**
 * @author zhenyu.nie created on 2019 2019/12/30 18:58
 */
public interface ProfilerContext {

    String getId();

    long getStartTime();

    long getIntervalMs();

    String getStatus();

    void start();

    void stop();

    void tryStop();
}
