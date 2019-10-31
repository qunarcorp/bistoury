package qunar.tc.bistoury.instrument.client.profiler;

/**
 * @author cai.wen created on 2019/10/22 20:16
 */
public class AgentProfilerContext {

    private static boolean isProfiling = false;

    private static long startTime = -1;

    public synchronized static void stopProfiling() {
        startTime = -1;
        isProfiling = false;
    }

    public synchronized static void startProfiling() {
        startTime = System.currentTimeMillis();
        isProfiling = true;
    }

    public synchronized static boolean isProfiling() {
        return isProfiling;
    }

    public synchronized static long getStartTime() {
        return startTime;
    }

}
