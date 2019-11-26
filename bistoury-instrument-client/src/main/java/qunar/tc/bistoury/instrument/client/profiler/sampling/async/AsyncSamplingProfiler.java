package qunar.tc.bistoury.instrument.client.profiler.sampling.async;

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.NamedThreadFactory;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.Profiler;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.*;

/**
 * @author cai.wen created on 2019/11/11 17:01
 * 先只支持生成火焰图
 */
public class AsyncSamplingProfiler implements Profiler {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("async-sampling-shutdown"));

    private volatile Lock lock;

    private Long frequencyMillis;

    private Long durationSeconds;

    private String event;

    private long startTime;

    private String profilerId;

    private String rootPath;

    private boolean threads;

    private boolean stopped;

    public AsyncSamplingProfiler(Map<String, String> params) {
        frequencyMillis = Long.parseLong(params.get(FREQUENCY));
        durationSeconds = Long.parseLong(params.get(DURATION));
        event = params.get(EVENT);
        profilerId = params.get(PROFILER_ID);
        rootPath = params.get(TMP_DIR) + File.separator + "bistoury-profiler";
        threads = Boolean.parseBoolean(params.get(THREADS));
    }

    @Override
    public void startup(InstrumentInfo instrumentInfo) {
        lock = instrumentInfo.getLock();
        lock.lock();
        try {
            String command = createProfilerCommand(ProfilerCommand.ProfilerAction.start);
            doRunCommand(command);
            AgentProfilerContext.startProfiling(frequencyMillis);
            AgentProfilerContext.setProfilerId(profilerId);
            startTime = System.currentTimeMillis();
            scheduleClose(durationSeconds);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() {
        lock.lock();
        try {
            if (stopped) {
                logger.warn("", "profiler is already stopped. profilerId: {}", profilerId);
                return;
            }
            if (!AgentProfilerContext.isProfiling()) {
                logger.info("async profiler is already stop.");
                return;
            }
            String command = createProfilerCommand(ProfilerCommand.ProfilerAction.stop);
            doRunCommand(command);
            AgentProfilerContext.stopProfiling();
            stopped = true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void destroy() {
        stop();
    }

    private void scheduleClose(long durationSeconds) {
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                stop();
            }
        }, durationSeconds, TimeUnit.SECONDS);
    }

    private void doRunCommand(String command) {
        try {
            Manager.execute(command);
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
            String profilerPath = rootPath + File.separator + profilerId
                    + "-" + (System.currentTimeMillis() - startTime) / 1000
                    + "-" + event;
            new File(profilerPath).mkdirs();
            String fileName = "async" + ".svg";
            logger.info("", "async file. path: {} file: {}", profilerPath, fileName);
            String file = new File(profilerPath, fileName).getAbsolutePath();
            command.setFile(file);
        }
        command.setThreads(threads);
        return command.getRealCommand();
    }
}
