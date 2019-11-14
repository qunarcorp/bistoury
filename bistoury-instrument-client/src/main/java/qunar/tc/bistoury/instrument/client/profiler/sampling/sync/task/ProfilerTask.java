package qunar.tc.bistoury.instrument.client.profiler.sampling.sync.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggerHelper;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.instrument.client.profiler.sampling.sync.Manager;
import qunar.tc.bistoury.instrument.client.profiler.sampling.sync.runtime.ProfilerDataRecorder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen created on 2019/10/15 15:55
 */
public class ProfilerTask implements Task {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final long frequency;

    public ProfilerTask(long frequency) {
        this.frequency = frequency;
    }

    private final ProfilerDataRecorder dataRecorder = new ProfilerDataRecorder();

    private final ThreadFactory profilerThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat(Manager.profilerThreadPoolName)
            .build();

    private final ScheduledExecutorService profilerExecutor = Executors
            .newSingleThreadScheduledExecutor(profilerThreadFactory);

    public synchronized void init() {
        logger.info("start init profiler task");
        profilerExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    dataRecorder.record();
                } catch (Exception e) {
                    BistouryLoggerHelper.error(e, "dump error. frequency: {}", frequency);
                }
            }
        }, 0, frequency, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        profilerExecutor.shutdown();
    }
}
