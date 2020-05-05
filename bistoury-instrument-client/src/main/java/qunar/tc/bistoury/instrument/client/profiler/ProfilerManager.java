package qunar.tc.bistoury.instrument.client.profiler;

import com.google.common.base.Optional;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.instrument.client.profiler.async.AsyncProfilerStore;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;

/**
 * @author zhenyu.nie created on 2019 2019/12/30 17:28
 */
public class ProfilerManager {

    private static final Logger logger = BistouryLoggger.getLogger();

    private boolean inited = false;

    private ProfilerStore profilerStore;

    private final Lock lock;

    private final ProfilerInfo profilerInfo;

    public ProfilerManager(Lock lock, ScheduledExecutorService executor) {
        this.lock = lock;
        this.profilerInfo = new ProfilerInfo(lock, executor);
        this.profilerStore = new AsyncProfilerStore(profilerInfo);
    }

    public void init() {
        try {
            lock.lock();
            doInit();
        } catch (Throwable e) {
            logger.error("", "profiler manager clear error", e);
        } finally {
            lock.unlock();
        }
    }

    private void doInit() {
        if (!inited) {
            profilerStore.clear();
            if (profilerStore.isRunning()) {
                logger.warn("profiler is still running");
                throw new IllegalStateException("profiler is still running");
            }
        }
        inited = true;
    }

    public boolean isRunning() {
        try {
            lock.lock();
            return profilerStore.isRunning();
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        try {
            lock.lock();
            profilerStore.clear();
        } finally {
            lock.unlock();
        }
    }

    public void start(Map<String, String> config) {
        try {
            lock.lock();
            doInit();
            if (profilerStore.isRunning()) {
                logger.error("", "profiler is running");
                throw new RuntimeException("profiler is running");
            }

            profilerInfo.setProfilerContext(profilerStore.start(config));
        } finally {
            lock.unlock();
        }
    }

    public String status(String id) {
        try {
            lock.lock();
            doInit();
            if (isCurrentProfiler(id)) {
                return profilerInfo.getProfilerContext().getStatus();
            } else {
                return profilerStore.getStatus(id);
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean isCurrentProfiler(String id) {
        ProfilerContext profilerContext = profilerInfo.getProfilerContext();
        return profilerContext != null && profilerContext.getId().equals(id);
    }

    public void stop(String id) {
        try {
            lock.lock();
            doInit();
            if (isCurrentProfiler(id)) {
                profilerInfo.getProfilerContext().stop();
            }
        } finally {
            lock.unlock();
        }
    }

    public Optional<ProfilerContext> getCurrentProfiling() {
        try {
            lock.lock();
            ProfilerContext profilerContext = profilerInfo.getProfilerContext();
            if (profilerContext != null && ProfilerUtil.RUNNING_STATUS.equals(profilerContext.getStatus())) {
                return Optional.of(profilerContext);
            } else {
                return Optional.absent();
            }
        } finally {
            lock.unlock();
        }
    }
}
