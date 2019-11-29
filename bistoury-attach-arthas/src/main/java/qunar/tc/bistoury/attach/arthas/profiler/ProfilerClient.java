package qunar.tc.bistoury.attach.arthas.profiler;

import com.google.common.base.Strings;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClient;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.instrument.client.profiler.Profiler;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerFactory;
import qunar.tc.bistoury.instrument.client.profiler.sampling.Profilers;

import java.util.Map;

/**
 * @author cai.wen created on 2019/10/22 20:03
 */
public class ProfilerClient implements InstrumentClient {

    private static final Logger logger = BistouryLoggger.getLogger();

    private Profiler profiler;

    private boolean init = false;

    public synchronized void start(Map<String, String> config) {
        if (!init) {
            stopAll();
            init = true;
        }

        if (isRunning(profiler)) {
            logger.error("", "profiler is running: " + profiler.getId());
            throw new RuntimeException("profiler is running: " + profiler.getId());
        }

        logger.info("start profiler," + config);
        profiler = ProfilerFactory.create(config);
        profiler.start();
    }

    public synchronized String status(String id) {
        if (!init) {
            stopAll();
            init = true;
        }

        if (profiler != null && profiler.getId().equals(id)) {
            return profiler.getStatus();
        }

        return Profilers.findNotRunningStatus(id);
    }

    public synchronized void stop(String id) {
        if (!init) {
            stopAll();
            init = true;
        }

        if (profiler == null || Strings.isNullOrEmpty(profiler.getId())) {
            return;
        }

        if (profiler.getId().equals(id)) {
            logger.info("stop profiler " + id);
            profiler.stop();
        } else {
            logger.error("", "running profiler id not equal, current " + profiler.getId() + ", expect " + id);
            throw new RuntimeException("running profiler id not equal, current " + profiler.getId() + ", expect " + id);
        }
    }

    public synchronized void stopAll() {
        qunar.tc.bistoury.instrument.client.profiler.sampling.sync.Manager.stop();
        qunar.tc.bistoury.instrument.client.profiler.sampling.async.Manager.stop();
    }

    private boolean isRunning(Profiler profiler) {
        return profiler != null && ProfilerUtil.RUNNING_STATUS.equals(profiler.getStatus());
    }

    @Override
    public synchronized void destroy() {
        if (isRunning(profiler)) {
            profiler.stop();
        }
    }
}
