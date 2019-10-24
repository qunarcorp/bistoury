package qunar.tc.bistoury.instrument.client.profiler.sampling;

import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.sampling.task.DumpTask;
import qunar.tc.bistoury.instrument.client.profiler.sampling.task.ProfilerTask;
import qunar.tc.bistoury.instrument.client.profiler.sampling.task.Task;
import qunar.tc.bistoury.instrument.client.profiler.util.trie.Trie;

import java.io.File;

/**
 * @author cai.wen created on 2019/10/17 10:51
 */
public class Manager {

    private static final boolean isDebugMode = true;

    private static String dumpDir = System.getProperty("user.home") + File.separator + "bistoury-profiler";

    public static final String profilerThreadPoolName = "bistoury-profile";

    public static final String profilerThreadPoolDumpName = "bistoury-profile-dump";

    public static final String runnableDataPath = getFullPath("runnable-traces.txt");
    public static final String filterRunnableDataPath = getFullPath("filter-runnable-traces.txt");

    public static final String blockedDataPath = getFullPath("blocked-traces.txt");
    public static final String filterBlockedDataPath = getFullPath("filter-blocked-traces.txt");

    public static final String timedWaitingDataPath = getFullPath("timed-waiting-traces.txt");
    public static final String filterTimedWaitingDataPath = getFullPath("filter-timed-waiting-traces.txt");

    public static final String waitingDataPath = getFullPath("waiting-traces.txt");
    public static final String filterWaitingDataPath = getFullPath("filter-waiting-traces.txt");

    public static final String allStatePath = getFullPath("all-state-traces.txt");
    public static final String filterAllStatePath = getFullPath("filter-all-state-traces.txt");

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

        if (isDebugMode) {
            new File(dumpDir).delete();
        }
        new File(dumpDir).mkdirs();
    }

    public static boolean isCompactClass(String className) {
        return compactPrefixPackage.containsPrefixNode(className);
    }

    private static Task profilerTask;

    private static Task dumpTask;

    public static synchronized void init(int durationSeconds, int frequencyMillis) {
        profilerTask = new ProfilerTask(frequencyMillis);
        dumpTask = new DumpTask(durationSeconds);

        profilerTask.init();
        dumpTask.init();
        AgentProfilerContext.startProfiling();
    }

    public synchronized static void stop() {
        if (profilerTask != null) {
            profilerTask.destroy();
            profilerTask = null;
        }

        if (dumpTask != null) {
            dumpTask.destroy();
            dumpTask = null;
        }

        AgentProfilerContext.stopProfiling();
    }

    public static boolean isDebugMode() {
        return isDebugMode;
    }

    private static String getFullPath(String fileName) {
        return dumpDir + File.separator + fileName;
    }
}
