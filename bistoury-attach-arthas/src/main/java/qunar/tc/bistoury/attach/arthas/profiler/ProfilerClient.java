package qunar.tc.bistoury.attach.arthas.profiler;

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClient;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.Mode;
import qunar.tc.bistoury.instrument.client.profiler.Profiler;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerFactory;

import java.util.Map;

/**
 * @author cai.wen created on 2019/10/22 20:03
 */
public class ProfilerClient implements InstrumentClient {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final InstrumentInfo instrumentInfo;

    private volatile Profiler profiler;

    public ProfilerClient(InstrumentInfo instrumentInfo) {
        this.instrumentInfo = instrumentInfo;
    }

    public void startProfiler(Mode mode, Map<String, Object> config) {
        if (AgentProfilerContext.isProfiling()) {
            throw new RuntimeException("start profiler error. target vm is profiling");
        }
        logger.info("start init profiler client");
        profiler = ProfilerFactory.create(mode, config);
        profiler.startup(instrumentInfo);
    }

    public void stopProfiler(){
        profiler.stop();
    }

    @Override
    public synchronized void destroy() {
        profiler.destroy();
    }
}
