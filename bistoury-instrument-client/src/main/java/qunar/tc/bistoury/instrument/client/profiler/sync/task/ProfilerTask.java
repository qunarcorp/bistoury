package qunar.tc.bistoury.instrument.client.profiler.sync.task;

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggerHelper;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.instrument.client.profiler.sync.runtime.ProfilerDataRecorder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen created on 2019/10/15 15:55
 */
public class ProfilerTask implements Task {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final long interval;

    private volatile boolean stopped = false;
    private final ScheduledExecutorService profilerExecutor;


    public ProfilerTask(long interval, ScheduledExecutorService profilerExecutor) {
        this.interval = interval;
        this.profilerExecutor = profilerExecutor;
    }

    private final ProfilerDataRecorder dataRecorder = new ProfilerDataRecorder();


    public synchronized void init() {
        logger.info("start init profiler task");
        scheduleRecord();
    }

    private void scheduleRecord() {
        profilerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    dataRecorder.record();
                    if (!stopped) {
                        scheduleRecord();
                    }
                } catch (Exception e) {
                    logger.error("", BistouryLoggerHelper.formatMessage("dump error. interval: {}", interval), e);
                }
            }
        }, interval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        stopped = true;
    }
}
