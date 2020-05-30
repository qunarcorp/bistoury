package qunar.tc.bistoury.attach.arthas.profiler;

import com.google.common.base.Optional;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClient;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.NamedThreadFactory;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerManager;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhenyu.nie created on 2019 2019/12/30 17:19
 */
public class GProfilerClient implements InstrumentClient {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("bistoury-profiler-shutdown"));

    private final ProfilerManager manager = new ProfilerManager(new ReentrantLock(), executor);

    GProfilerClient() {
        manager.init();
    }

    void start(Map<String, String> config) {
        logger.info("start profiler," + config);
        manager.start(config);
    }

    String status(String id) {
        return manager.status(id);
    }

    boolean isRunning() {
        return manager.isRunning();
    }

    Optional<ProfilerContext> getCurrentProfiling() {
        return manager.getCurrentProfiling();
    }

    void stop(String id) {
        logger.info("stop profiler " + id);
        manager.stop(id);
    }

    void clear() {
        logger.info("clear profiler");
        manager.clear();
    }

    @Override
    public void destroy() {
        executor.shutdownNow();
        try {
            manager.clear();
        } catch (Throwable t) {
            logger.error("", "destroy profiler client error", t);
        }
    }
}
