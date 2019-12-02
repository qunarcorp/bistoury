package qunar.tc.bistoury.instrument.client.profiler.sampling.async;

import com.google.common.base.Throwables;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.NamedThreadFactory;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.instrument.client.profiler.Profiler;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.*;

/**
 * @author cai.wen created on 2019/11/11 17:01
 */
public class AsyncSamplingProfiler implements Profiler {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final long frequencyMillis;

    private final long durationSeconds;

    private final String event;

    private long startTime;

    private final String profilerId;

    private final String profilerDir;

    private final boolean threads;

    private final ScheduledExecutorService executor;

    private volatile String status;

    public AsyncSamplingProfiler(Map<String, String> params) {
        frequencyMillis = Long.parseLong(params.get(FREQUENCY));
        durationSeconds = Long.parseLong(params.get(DURATION));
        event = params.get(EVENT);
        profilerId = params.get(PROFILER_ID);
        threads = Boolean.parseBoolean(params.get(THREADS));
        profilerDir = params.get(TMP_DIR);
        executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("async-profiler-shutdown"));
    }

    @Override
    public String getId() {
        return profilerId;
    }

    @Override
    public String getStatus() {
        if (ProfilerUtil.ERROR_STATUS.equals(status) || ProfilerUtil.FINISH_STATUS.equals(status)) {
            return status;
        }
        String newStatus = findStatus();
        if (!status.equals(newStatus)) {
            status = newStatus;
        }
        return newStatus;
    }

    //async-profiler profiler.cpp:876
    private static final String NOT_ACTIVE_PREFIX = "Profiler is not active";

    private String findStatus() {
        String statusResult = doRunCommand("status");
        if (statusResult.startsWith(NOT_ACTIVE_PREFIX)) {
            return ProfilerUtil.FINISH_STATUS;
        } else {
            return ProfilerUtil.RUNNING_STATUS;
        }
    }

    @Override
    public void start() {
        try {
            String preProfilerStatus = findPreProfilerStatus();
            if (ProfilerUtil.RUNNING_STATUS.equals(preProfilerStatus)) {
                logger.warn("", "unknown profiler running before, try stop");
                stopPreProfiler();
            }
        } catch (Throwable e) {
            status = ProfilerUtil.ERROR_STATUS;
            logger.error("", "stop unknown profiler error", e);
            return;
        }

        try {
            String command = createProfilerCommand(ProfilerCommand.ProfilerAction.start);
            doRunCommand(command);
            status = ProfilerUtil.RUNNING_STATUS;
            startTime = System.currentTimeMillis();
        } catch (Throwable e) {
            status = ProfilerUtil.ERROR_STATUS;
            logger.error("", "start profiler error", e);
            Throwables.propagate(e);
            return;
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

    private void stopPreProfiler() {
        String command = createProfilerCommand(ProfilerCommand.ProfilerAction.stop);
        doRunCommand(command);
    }

    private String findPreProfilerStatus() {
        return findStatus();
    }

    @Override
    public void stop() {
        String command = createProfilerCommand(ProfilerCommand.ProfilerAction.stop);
        doRunCommand(command);
        status = ProfilerUtil.FINISH_STATUS;
        executor.shutdownNow();
    }

    private void delayStop(int count, int delay) {
        try {
            stop();
        } catch (Throwable e) {
            logger.error("", "stop profiler error, " + profilerId, e);
            stop(count - 1, delay);
        }
    }

    private void stop(final int count, final int delay) {
        if (count <= 0) {
            return;
        }

        executor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    stop();
                } catch (Throwable e) {
                    logger.error("", "stop profiler error, " + profilerId, e);
                    stop(count - 1, delay);
                }
            }
        }, delay, TimeUnit.SECONDS);
    }

    private void scheduleClose(long durationSeconds) {
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                delayStop(3, 3);
            }
        }, durationSeconds, TimeUnit.SECONDS);
    }

    private String doRunCommand(String command) {
        try {
            return Manager.execute(command);
        } catch (Exception e) {
            throw new RuntimeException("execute async command error. command: " + command, e);
        }
    }

    private String createProfilerCommand(ProfilerCommand.ProfilerAction action) {
        ProfilerCommand command = new ProfilerCommand();
        command.setAction(action);
        if (action == ProfilerCommand.ProfilerAction.start) {
            command.setEvent(event);
            command.setInterval(frequencyMillis * 1000000);
        }
        if (action == ProfilerCommand.ProfilerAction.stop) {
            String profilerPath = profilerDir + File.separator + profilerId
                    + "-" + (System.currentTimeMillis() - startTime) / 1000
                    + "-" + event;
            new File(profilerPath).mkdirs();
            String fileName = "async" + ".collapsed";
            logger.info("", "async file. path: {} file: {}", profilerPath, fileName);
            String file = new File(profilerPath, fileName).getAbsolutePath();
            command.setFile(file);
        }
        command.setThreads(threads);
        return command.getRealCommand();
    }
}
