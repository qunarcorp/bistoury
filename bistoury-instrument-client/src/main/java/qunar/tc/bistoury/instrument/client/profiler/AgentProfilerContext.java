package qunar.tc.bistoury.instrument.client.profiler;

/**
 * @author cai.wen created on 2019/10/22 20:16
 */
public class AgentProfilerContext {

    private static boolean isProfiling = false;

    private static long startTime = -1;

    private static String profilerId;

    private static long intervalMillis;

    private static long intervalNs;

    public synchronized static void stopProfiling() {
        startTime = -1;
        isProfiling = false;
    }

    public synchronized static void startProfiling(long intervalMillis) {
        startTime = System.currentTimeMillis();
        AgentProfilerContext.intervalMillis = intervalMillis;
        intervalNs = intervalMillis * 1000000;
        isProfiling = true;
    }

    public synchronized static boolean isProfiling() {
        return isProfiling;
    }

    public synchronized static long getStartTime() {
        return startTime;
    }

    public synchronized static String getProfilerId() {
        return profilerId;
    }

    public synchronized static void setProfilerId(String profilerId) {
        AgentProfilerContext.profilerId = profilerId;
    }

    public static long getIntervalMillis() {
        return intervalMillis;
    }

    public static long getIntervalNs() {
        return intervalNs;
    }
}
