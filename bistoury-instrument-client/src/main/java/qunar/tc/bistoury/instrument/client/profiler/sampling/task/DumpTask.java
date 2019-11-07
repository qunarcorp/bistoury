package qunar.tc.bistoury.instrument.client.profiler.sampling.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.instrument.client.profiler.sampling.Manager;
import qunar.tc.bistoury.instrument.client.profiler.sampling.runtime.ProfilerData;
import qunar.tc.bistoury.instrument.client.profiler.sampling.runtime.ProfilerDataDumper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author cai.wen created on 2019/10/17 11:25
 */
public class DumpTask implements Task {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final int duration;

    private final Lock lock = new ReentrantLock();

    private boolean isDump = false;

    public DumpTask(int duration) {
        this.duration = duration;
    }

    private final ProfilerDataDumper dataDumper = new ProfilerDataDumper();

    private static final ThreadFactory dumpThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat(Manager.profilerThreadPoolDumpName)
            .build();

    private final ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor(dumpThreadFactory);

    @Override
    public void init() {
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                doDump();
                Manager.stop();
            }
        }, duration, TimeUnit.SECONDS);
    }

    private void doDump() {
        lock.lock();
        try {
            if (isDump) {
                logger.warn("profiler data is already dump.");
                return;
            }
            isDump = true;
            dataDumper.dump();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() {
        doDump();
        ProfilerData.reset();
        scheduledExecutorService.shutdownNow();
    }
}
