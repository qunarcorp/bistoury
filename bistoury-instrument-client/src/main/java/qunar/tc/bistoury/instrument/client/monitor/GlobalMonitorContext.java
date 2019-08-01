package qunar.tc.bistoury.instrument.client.monitor;

import com.google.common.collect.Maps;
import qunar.tc.bistoury.instrument.client.location.ResolvedSourceLocation;

import java.util.Map;

/**
 * @author: leix.xie
 * @date: 2019/1/15 20:01
 * @describe：
 */
public class GlobalMonitorContext {
    private static final Map<String, Boolean> monitors = Maps.newHashMap();
    private static final String SEPARATOR = "|";

    public static void addMonitor(final ResolvedSourceLocation location) {
        synchronized (monitors) {
            monitors.put(location.getClassSignature() + SEPARATOR + location.getMethodName() + SEPARATOR + location.getMethodDesc(), true);
        }
    }

    public static boolean check(final ResolvedSourceLocation location) {
        synchronized (monitors) {
            return monitors.containsKey(location.getClassSignature() + SEPARATOR + location.getMethodName() + SEPARATOR + location.getMethodDesc());
        }
    }

    public static void destroy() {
        synchronized (monitors) {
            monitors.clear();
        }
    }
}
