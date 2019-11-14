package qunar.tc.bistoury.instrument.client.profiler.sampling.sync;

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;
import qunar.tc.bistoury.instrument.client.profiler.Profiler;

import java.util.concurrent.locks.Lock;

/**
 * @author cai.wen created on 2019/10/23 11:33
 */
public class SamplingProfiler implements Profiler {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final long durationSeconds;

    private final long frequencyMillis;

    private final String profilerId;

    private final String tempDir;

    private volatile Lock lock;

    public SamplingProfiler(long durationSeconds, long frequencyMillis, String profilerId, String tempDir) {
        this.durationSeconds = durationSeconds;
        this.frequencyMillis = frequencyMillis;
        this.profilerId = profilerId;
        this.tempDir = tempDir;
    }

    @Override
    public void startup(InstrumentInfo instrumentInfo) {
        logger.info("start add sampling profiler.");
        lock = instrumentInfo.getLock();
        lock.lock();
        try {
            Manager.init(durationSeconds, frequencyMillis, profilerId, tempDir);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() {
        lock.lock();
        try {
            Manager.stop();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void destroy() {
        logger.info("destroy sampling profiler.");
        lock.lock();
        try {
            Manager.stop();
        } finally {
            lock.unlock();
        }
    }
}
