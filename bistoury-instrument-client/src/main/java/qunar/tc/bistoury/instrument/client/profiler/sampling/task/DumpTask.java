package qunar.tc.bistoury.instrument.client.profiler.sampling.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import qunar.tc.bistoury.instrument.client.profiler.sampling.Manager;
import qunar.tc.bistoury.instrument.client.profiler.sampling.runtime.ProfilerData;
import qunar.tc.bistoury.instrument.client.profiler.sampling.runtime.ProfilerDataDumper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen created on 2019/10/17 11:25
 */
public class DumpTask implements Task {

    private final int duration;

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
                dataDumper.dump();
                Manager.stop();
            }
        }, duration, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() {
        ProfilerData.reset();
    }
}
