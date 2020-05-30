package qunar.tc.bistoury.instrument.client.profiler.async;

import com.google.common.base.Throwables;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerInfo;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.DURATION;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.EVENT;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.INTERVAL;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.PROFILER_ID;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.THREADS;

/**
 * @author zhenyu.nie created on 2019 2019/12/31 15:21
 */
public class AsyncProfilerContext implements ProfilerContext {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final AsyncProfilerStore store;

    private final long intervalMillis;

    private final long durationSeconds;

    private final String event;

    private long startTime;

    private final String profilerId;

    private final boolean threads;

    private final ProfilerInfo profilerInfo;

    private volatile String status;

    public AsyncProfilerContext(AsyncProfilerStore store, Map<String, String> params, ProfilerInfo profilerInfo) {
        this.store = store;
        intervalMillis = Long.parseLong(params.get(INTERVAL));
        durationSeconds = Long.parseLong(params.get(DURATION));
        event = params.get(EVENT);
        profilerId = params.get(PROFILER_ID);
        threads = Boolean.parseBoolean(params.get(THREADS));
        this.profilerInfo = profilerInfo;
    }

    @Override
    public String getId() {
        return profilerId;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getIntervalMs() {
        return intervalMillis;
    }

    @Override
    public String getStatus() {
        if (ProfilerUtil.ERROR_STATUS.equals(status) || ProfilerUtil.FINISH_STATUS.equals(status)) {
            return status;
        }
        String newStatus = store.getStatus();
        if (!newStatus.equals(status)) {
            status = newStatus;
        }
        return newStatus;
    }

    @Override
    public void start() {
        try {
            store.start(this);
            status = ProfilerUtil.RUNNING_STATUS;
            startTime = System.currentTimeMillis();
        } catch (Throwable e) {
            status = ProfilerUtil.ERROR_STATUS;
            logger.error("", "start profiler error", e);
            throw e;
        }

        try {
            scheduleClose(durationSeconds);
        } catch (Throwable e) {
            status = ProfilerUtil.ERROR_STATUS;
            logger.error("", "create schedule close error, " + profilerId, e);
            try {
                stop();
            } catch (Throwable t) {
                logger.error("", "stop error profiler error, " + profilerId, t);
                Throwables.propagate(t);
            }
        }
    }

    private void scheduleClose(long durationSeconds) {
        ScheduledExecutorService executor = profilerInfo.getExecutor();
        executor.schedule(new RetryCloseRunnable(profilerInfo, this, 3, 3), durationSeconds, TimeUnit.SECONDS);
    }

    private static class RetryCloseRunnable implements Runnable {

        private final ProfilerInfo profilerInfo;

        private final AsyncProfilerContext context;

        private final int retryCount;

        private final int delaySec;

        public RetryCloseRunnable(ProfilerInfo profilerInfo,
                                  AsyncProfilerContext context,
                                  int retryCount,
                                  int delaySec) {
            this.profilerInfo = profilerInfo;
            this.context = context;
            this.retryCount = retryCount;
            this.delaySec = delaySec;
        }

        @Override
        public void run() {
            Lock lock = profilerInfo.getLock();
            try {
                lock.lock();
                context.tryStop();
            } catch (Throwable e) {
                logger.error("", "close profiler error", e);
                int newRetryCount = retryCount - 1;
                if (newRetryCount > 0) {
                    ScheduledExecutorService executor = profilerInfo.getExecutor();
                    executor.schedule(new RetryCloseRunnable(profilerInfo, context, newRetryCount, delaySec), delaySec, TimeUnit.SECONDS);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void stop() {
        store.stop(this);
    }

    @Override
    public void tryStop() {
        ProfilerContext context = profilerInfo.getProfilerContext();
        if (profilerId.equals(context.getId())) {
            store.tryStop(this);
        }
    }

    String createStartCommand() {
        ProfilerCommand command = new ProfilerCommand();
        command.setAction(ProfilerCommand.ProfilerAction.start);
        command.setEvent(event);
        command.setInterval(intervalMillis * 1000000);
        command.setThreads(threads);
        return command.getRealCommand();
    }

    String createStopCommand() {
        ProfilerCommand command = new ProfilerCommand();
        command.setAction(ProfilerCommand.ProfilerAction.stop);
        String profilerPath = BistouryStore.getProfilerTempPath() + File.separator + profilerId
                + "-" + (System.currentTimeMillis() - startTime) / 1000
                + "-" + event;
        new File(profilerPath).mkdirs();
        String fileName = "async" + ".collapsed";
        logger.info("", "async file. path: {} file: {}", profilerPath, fileName);
        String file = new File(profilerPath, fileName).getAbsolutePath();
        command.setFile(file);
        command.setThreads(threads);
        return command.getRealCommand();
    }
}
