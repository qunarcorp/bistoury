package qunar.tc.bistoury.instrument.client.profiler.sampling.sync;

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggerHelper;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.sampling.sync.task.DumpTask;
import qunar.tc.bistoury.instrument.client.profiler.sampling.sync.task.ProfilerTask;
import qunar.tc.bistoury.instrument.client.profiler.sampling.sync.task.Task;
import qunar.tc.bistoury.instrument.client.profiler.util.trie.Trie;

import java.io.File;

/**
 * @author cai.wen created on 2019/10/17 10:51
 */
public class Manager {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final boolean isDebugMode = false;

    public static final String profilerThreadPoolName = "bistoury-profile";

    public static final String profilerThreadPoolDumpName = "bistoury-profile-dump";

    private static final String runnableDataPath = "runnable-traces.txt";
    private static final String filterRunnableDataPath = "filter-runnable-traces.txt";
    private static final String blockedDataPath = "blocked-traces.txt";
    private static final String filterBlockedDataPath = "filter-blocked-traces.txt";
    private static final String timedWaitingDataPath = "timed-waiting-traces.txt";
    private static final String filterTimedWaitingDataPath = "filter-timed-waiting-traces.txt";
    private static final String waitingDataPath = "waiting-traces.txt";
    private static final String filterWaitingDataPath = "filter-waiting-traces.txt";
    private static final String allStatePath = "all-state-traces.txt";
    private static final String filterAllStatePath = "filter-all-state-traces.txt";

    private static volatile String profilerId;

    private static final Trie compactPrefixPackage = new Trie();

    static {
        compactPrefixPackage.insert("java.");
        compactPrefixPackage.insert("javax.");
        compactPrefixPackage.insert("sun.");
        compactPrefixPackage.insert("org.springframework.");
        compactPrefixPackage.insert("org.jboss.");
        compactPrefixPackage.insert("org.apache.");
        compactPrefixPackage.insert("com.sun.");
        compactPrefixPackage.insert("org.mybatis.");
        compactPrefixPackage.insert("com.mysql.");
        compactPrefixPackage.insert("io.netty.");
        compactPrefixPackage.insert("com.google.");
        compactPrefixPackage.insert("ch.qos.");
        compactPrefixPackage.insert("org.slf4j.");
        compactPrefixPackage.insert("io.termd.core.");
        compactPrefixPackage.insert("com.taobao.arthas.");
        compactPrefixPackage.insert("com.taobao.middleware.");
    }

    public static boolean isCompactClass(String className) {
        return compactPrefixPackage.containsPrefixNode(className);
    }

    private static Task profilerTask;

    private static Task dumpTask;

    private static volatile long startTime;

    private static void createDumpPath(String profilerDir) {
        BistouryStore.PROFILER_ROOT_PATH = profilerDir;
        BistouryStore.PROFILER_TEMP_PATH = profilerDir + File.separator + "tmp";
        if (isDebugMode) {
            new File(BistouryStore.PROFILER_ROOT_PATH).delete();
            new File(BistouryStore.PROFILER_TEMP_PATH).delete();
        }
        new File(BistouryStore.PROFILER_ROOT_PATH).mkdirs();
        new File(BistouryStore.PROFILER_TEMP_PATH + File.separator + profilerId).mkdirs();
    }

    public static synchronized void init(long durationSeconds, long frequencyMillis, String profilerId, String profilerDir) {
        Manager.profilerId = profilerId;
        AgentProfilerContext.setProfilerId(profilerId);
        profilerTask = new ProfilerTask(frequencyMillis);
        dumpTask = new DumpTask(durationSeconds);
        createDumpPath(profilerDir);

        profilerTask.init();
        dumpTask.init();
        startTime = System.currentTimeMillis();
        AgentProfilerContext.startProfiling(frequencyMillis);
    }


    public synchronized static void stop() {
        stopTask(profilerTask);
        stopTask(dumpTask);
        AgentProfilerContext.stopProfiling();
    }

    public static void renameResult(long dumpTime) {
        long durationSeconds = (dumpTime - startTime) / 1000;
        File preDumpPath = new File(BistouryStore.PROFILER_TEMP_PATH + File.separator + profilerId);
        File realDumpPath = new File(BistouryStore.PROFILER_ROOT_PATH + File.separator + profilerId + "-" + durationSeconds);
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
        String profilerIdPath = BistouryStore.PROFILER_TEMP_PATH + File.separator + profilerId;
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
