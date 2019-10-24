package qunar.tc.bistoury.instrument.client.profiler;

/**
 * @author cai.wen created on 2019/10/22 20:16
 */
public class AgentProfilerContext {

    private static boolean isProfiling = false;

    public synchronized static void stopProfiling() {
        isProfiling = false;
    }

    public synchronized static void startProfiling() {
        isProfiling = true;
    }

    public synchronized static boolean isProfiling() {
        return isProfiling;
    }
}
