package qunar.tc.bistoury.instrument.client.profiler.sampling;

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

    private final int durationSeconds;

    private final int frequencyMillis;

    private final String profilerId;

    private final String tempDir;

    private volatile InstrumentInfo instrumentInfo;

    public SamplingProfiler(int durationSeconds, int frequencyMillis, String profilerId, String tempDir) {
        this.durationSeconds = durationSeconds;
        this.frequencyMillis = frequencyMillis;
        this.profilerId = profilerId;
        this.tempDir = tempDir;
    }

    @Override
    public void startup(InstrumentInfo instrumentInfo) {
        Lock lock = instrumentInfo.getLock();
        logger.info("start add sampling profiler.");
        try {
            lock.lock();
            Manager.init(durationSeconds, frequencyMillis, profilerId, tempDir);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void destroy() {
        Lock lock = instrumentInfo.getLock();
        logger.info("destroy sampling profiler.");
        try {
            lock.lock();
            Manager.stop();
        } finally {
            lock.unlock();
        }
    }
}
