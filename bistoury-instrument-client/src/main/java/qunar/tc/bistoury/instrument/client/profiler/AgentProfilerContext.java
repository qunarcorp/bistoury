package qunar.tc.bistoury.instrument.client.profiler;


import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author cai.wen created on 2019/10/22 20:16
 */
public class AgentProfilerContext {

    private static boolean isProfiling = false;

    private static long startTime = -1;

    private static String profilerId;

    private static long intervalMillis;

    private static long intervalNs;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM-dd-yyyy HH:mm:ss");

    public synchronized static void stopProfiling() {
        startTime = -1;
        isProfiling = false;
        profilerId = null;
    }

    public synchronized static void startProfiling(String profilerId, long intervalMillis) {
        startTime = System.currentTimeMillis();
        AgentProfilerContext.intervalMillis = intervalMillis;
        intervalNs = intervalMillis * 1000000;
        isProfiling = true;
        AgentProfilerContext.profilerId = profilerId;
    }

    public synchronized static boolean isProfiling() {
        return isProfiling;
    }

    public synchronized static String getStartTime() {
        return dateTimeFormatter.print(startTime);
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
