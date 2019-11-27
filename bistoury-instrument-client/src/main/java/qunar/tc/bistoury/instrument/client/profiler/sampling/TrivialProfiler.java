package qunar.tc.bistoury.instrument.client.profiler.sampling;

import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.instrument.client.profiler.Profiler;

/**
 * @author zhenyu.nie created on 2019 2019/11/27 10:34
 */
class TrivialProfiler implements Profiler {

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getStatus() {
        return ProfilerUtil.ERROR_STATUS;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
