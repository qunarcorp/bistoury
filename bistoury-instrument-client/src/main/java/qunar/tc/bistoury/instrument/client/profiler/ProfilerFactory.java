package qunar.tc.bistoury.instrument.client.profiler;

import qunar.tc.bistoury.instrument.client.profiler.sampling.async.AsyncSamplingProfiler;
import qunar.tc.bistoury.instrument.client.profiler.sampling.sync.SamplingProfiler;

import java.util.Map;

/**
 * @author cai.wen created on 2019/10/23 11:13
 */
public class ProfilerFactory {

    public static Profiler create(Mode mode, Map<String, Object> config) {
        mode = getMode(mode);
        switch (mode) {
            case sampler:
                return new SamplingProfiler(config);
            case async_sampler:
                return new AsyncSamplingProfiler(config);
            default:
                throw new RuntimeException("no kind of mode: " + mode);
        }
    }

    private static Mode getMode(Mode mode) {
        if (mode == null) {
            return Mode.sampler;
        }
        return mode;
    }

}
