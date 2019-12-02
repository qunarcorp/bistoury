package qunar.tc.bistoury.instrument.client.profiler.sampling.sync;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.instrument.client.profiler.Profiler;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.TMP_DIR;

/**
 * @author cai.wen created on 2019/10/23 11:33
 */
public class SamplingProfiler implements Profiler {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final long durationSeconds;

    private final long frequencyMillis;

    private final String profilerId;

    private volatile String status;

    private final String profilerDir;

    private static final ThreadFactory dumpThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat(Manager.profilerThreadPoolDumpName)
            .build();

    private static final ScheduledExecutorService dumpExecutorService =
            Executors.newSingleThreadScheduledExecutor(dumpThreadFactory);

    public SamplingProfiler(Map<String, String> config) {
        this.frequencyMillis = Long.parseLong(config.get(ProfilerConstants.FREQUENCY));
        this.durationSeconds = Long.parseLong(config.get(ProfilerConstants.DURATION));
        this.profilerId = config.get(ProfilerConstants.PROFILER_ID);
        profilerDir = config.get(TMP_DIR);
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
        Manager.init(frequencyMillis, profilerId, profilerDir);
        dumpExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                Manager.stop();
                status = ProfilerUtil.FINISH_STATUS;
            }
        }, durationSeconds, TimeUnit.SECONDS);
        status = ProfilerUtil.RUNNING_STATUS;
    }

    @Override
    public void stop() {
        logger.info("destroy sampling profiler.");
        Manager.stop();
        status = ProfilerUtil.FINISH_STATUS;
    }
}
