package qunar.tc.bistoury.instrument.client.profiler.sampling;

import qunar.tc.bistoury.instrument.client.profiler.Profiler;

/**
 * @author zhenyu.nie created on 2019 2019/11/27 10:32
 */
public class Profilers {

    public static final Profiler TRIVIAL_PROFILER = new TrivialProfiler();

    public static String findNotRunningStatus(String id) {
        // todo: 看看id对应的数据文件有没有，有就是ProfileUtil.FINISH，没有就是ProfileUtil.ERROR
        return null;
    }
}
