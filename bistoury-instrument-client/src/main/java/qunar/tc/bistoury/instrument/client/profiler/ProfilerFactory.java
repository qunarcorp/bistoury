package qunar.tc.bistoury.instrument.client.profiler;

import qunar.tc.bistoury.common.OsUtils;
import qunar.tc.bistoury.instrument.client.profiler.sampling.async.AsyncSamplingProfiler;
import qunar.tc.bistoury.instrument.client.profiler.sampling.sync.SamplingProfiler;

import java.util.Map;

import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.EVENT;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.MODE;

/**
 * @author cai.wen created on 2019/10/23 11:13
 */
public class ProfilerFactory {

    public static Profiler create(Map<String, String> config) {
        Mode mode = getMode(config);
        switch (mode) {
            case sampler:
                return new SamplingProfiler(config);
            case async_sampler: {
                if (config.get(EVENT) == null) {
                    config.put(EVENT, getPlatformEvent());
                }
                return new AsyncSamplingProfiler(config);
            }
            default:
                throw new RuntimeException("no kind of mode: " + mode);
        }
    }

    private static Mode getMode(Map<String, String> config) {
        String mode = config.get(MODE);
        return mode == null ? getPlatformMode() : Mode.codeOf(Integer.parseInt(mode));
    }

    private static Mode getPlatformMode() {
        if (OsUtils.isWindows()) {
            return Mode.sampler;
        }
        return Mode.async_sampler;
    }

    private static String getPlatformEvent() {
        if (OsUtils.isSupportPerf()) {
            return "cpu";
        }
        return "itimer";
    }
}
