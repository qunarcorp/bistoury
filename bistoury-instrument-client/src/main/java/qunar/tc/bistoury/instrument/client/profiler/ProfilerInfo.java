package qunar.tc.bistoury.instrument.client.profiler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;

/**
 * @author zhenyu.nie created on 2019 2019/12/31 17:17
 */
public class ProfilerInfo {

    private final Lock lock;

    private final ScheduledExecutorService executor;

    private ProfilerContext profilerContext;

    public ProfilerInfo(Lock lock, ScheduledExecutorService executor) {
        this.lock = lock;
        this.executor = executor;
    }

    public Lock getLock() {
        return lock;
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public ProfilerContext getProfilerContext() {
        return profilerContext;
    }

    public void setProfilerContext(ProfilerContext profilerContext) {
        this.profilerContext = profilerContext;
    }
}
