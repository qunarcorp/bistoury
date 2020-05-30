package qunar.tc.bistoury.instrument.client.profiler.sync;

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.Profiler;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen created on 2019/10/23 11:33
 */
public class SamplingProfiler implements Profiler {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final long durationSeconds;

    private final long intervalMillis;

    private final String profilerId;

    private volatile String status;

    private final String profilerDir;

    private final ScheduledExecutorService dumpExecutorService;

    public SamplingProfiler(Map<String, String> config, ScheduledExecutorService executor) {
        this.intervalMillis = Long.parseLong(config.get(ProfilerConstants.INTERVAL));
        this.durationSeconds = Long.parseLong(config.get(ProfilerConstants.DURATION));
        this.profilerId = config.get(ProfilerConstants.PROFILER_ID);
        dumpExecutorService = executor;
        profilerDir = BistouryStore.DEFAULT_PROFILER_TEMP_PATH;
    }

    @Override
    public String getId() {
        return profilerId;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void start() {
        logger.info("start add sampling profiler.");
        Manager.init(intervalMillis, profilerId, profilerDir, dumpExecutorService);
        dumpExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                stop();
            }
        }, durationSeconds, TimeUnit.SECONDS);
        status = ProfilerUtil.RUNNING_STATUS;
    }

    @Override
    public void stop() {
        logger.info("destroy sampling profiler.");
        Manager.stop();
        status = ProfilerUtil.FINISH_STATUS;
        AgentProfilerContext.stopProfiling();
    }
}
