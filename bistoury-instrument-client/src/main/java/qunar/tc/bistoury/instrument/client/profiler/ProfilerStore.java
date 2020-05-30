package qunar.tc.bistoury.instrument.client.profiler;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/12/31 14:54
 */
public interface ProfilerStore {

    boolean isRunning();

    ProfilerContext start(Map<String, String> config);

    String getStatus();

    String getStatus(String id);

    void clear();
}
