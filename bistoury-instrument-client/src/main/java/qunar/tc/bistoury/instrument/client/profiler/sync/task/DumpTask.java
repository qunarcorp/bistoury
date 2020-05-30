package qunar.tc.bistoury.instrument.client.profiler.sync.task;

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.instrument.client.profiler.sync.runtime.ProfilerData;
import qunar.tc.bistoury.instrument.client.profiler.sync.runtime.ProfilerDataDumper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author cai.wen created on 2019/10/17 11:25
 */
public class DumpTask implements Task {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final Lock lock = new ReentrantLock();

    private volatile boolean isDump = false;

    private final ProfilerDataDumper dataDumper;

    public DumpTask() {
        this.dataDumper = new ProfilerDataDumper();
    }

    private void doDump() {
        lock.lock();
        try {
            if (isDump) {
                logger.warn("profiler data is already dump.");
                return;
            }
            dataDumper.dump();
            isDump = true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {
        doDump();
        ProfilerData.reset();
    }
}
