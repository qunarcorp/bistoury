package qunar.tc.bistoury.instrument.client.profiler.sync;

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggerHelper;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.sync.task.DumpTask;
import qunar.tc.bistoury.instrument.client.profiler.sync.task.ProfilerTask;
import qunar.tc.bistoury.instrument.client.profiler.sync.task.Task;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author cai.wen created on 2019/10/17 10:51
 */
public class Manager {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final boolean isDebugMode = false;

    private static final String runnableDataPath = "runnable-traces.collapsed";
    private static final String filterRunnableDataPath = "filter-runnable-traces.collapsed";
    private static final String blockedDataPath = "blocked-traces.collapsed";
    private static final String filterBlockedDataPath = "filter-blocked-traces.collapsed";
    private static final String timedWaitingDataPath = "timed-waiting-traces.collapsed";
    private static final String filterTimedWaitingDataPath = "filter-timed-waiting-traces.collapsed";
    private static final String waitingDataPath = "waiting-traces.collapsed";
    private static final String filterWaitingDataPath = "filter-waiting-traces.collapsed";
    private static final String allStatePath = "all-state-traces.collapsed";
    private static final String filterAllStatePath = "filter-all-state-traces.collapsed";

    private static volatile String profilerId;

    private static Task profilerTask;

    private static Task dumpTask;

    private static volatile long startTime;

    private static void createDumpPath(String profilerDir) {
        if (isDebugMode) {
            new File(BistouryStore.DEFAULT_PROFILER_ROOT_PATH).delete();
            new File(BistouryStore.DEFAULT_PROFILER_TEMP_PATH).delete();
        }
        new File(BistouryStore.DEFAULT_PROFILER_ROOT_PATH).mkdirs();
        new File(BistouryStore.DEFAULT_PROFILER_TEMP_PATH + File.separator + profilerId).mkdirs();
    }

    public static synchronized void init(long intervalMillis, String profilerId, String profilerDir, ScheduledExecutorService executorService) {
        Manager.profilerId = profilerId;
        AgentProfilerContext.setProfilerId(profilerId);
        profilerTask = new ProfilerTask(intervalMillis, executorService);
        dumpTask = new DumpTask();
        createDumpPath(profilerDir);

        profilerTask.init();
        dumpTask.init();
        startTime = System.currentTimeMillis();
    }

    public synchronized static void stop() {
        stopTask(profilerTask);
        stopTask(dumpTask);
    }

    public static void renameResult(long dumpTime) {
        long durationSeconds = (dumpTime - startTime) / 1000;
        File preDumpPath = new File(BistouryStore.DEFAULT_PROFILER_TEMP_PATH + File.separator + profilerId);
        File realDumpPath = new File(BistouryStore.DEFAULT_PROFILER_ROOT_PATH + File.separator + profilerId + "-" + durationSeconds);
        preDumpPath.renameTo(realDumpPath);
    }

    private static void stopTask(Task task) {
        try {
            if (task != null) {
                task.stop();
            }
        } catch (Exception e) {
            logger.error("", BistouryLoggerHelper.formatMessage("destroy task error. task: {}", task), e);
        }
    }

    public static boolean isDebugMode() {
        return isDebugMode;
    }

    private static String getFullPath(String fileName) {
        String profilerIdPath = BistouryStore.DEFAULT_PROFILER_TEMP_PATH + File.separator + profilerId;
        return profilerIdPath + File.separator + fileName;
    }

    public static String getRunnableDataPath() {
        return getFullPath(runnableDataPath);
    }

    public static String getFilterRunnableDataPath() {
        return getFullPath(filterRunnableDataPath);
    }

    public static String getBlockedDataPath() {
        return getFullPath(blockedDataPath);
    }

    public static String getFilterBlockedDataPath() {
        return getFullPath(filterBlockedDataPath);
    }

    public static String getTimedWaitingDataPath() {
        return getFullPath(timedWaitingDataPath);
    }

    public static String getFilterTimedWaitingDataPath() {
        return getFullPath(filterTimedWaitingDataPath);
    }

    public static String getWaitingDataPath() {
        return getFullPath(waitingDataPath);
    }

    public static String getFilterWaitingDataPath() {
        return getFullPath(filterWaitingDataPath);
    }

    public static String getAllStatePath() {
        return getFullPath(allStatePath);
    }

    public static String getFilterAllStatePath() {
        return getFullPath(filterAllStatePath);
    }
}
