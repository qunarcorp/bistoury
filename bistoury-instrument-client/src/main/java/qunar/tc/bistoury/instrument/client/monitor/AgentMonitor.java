package qunar.tc.bistoury.instrument.client.monitor;

import com.google.common.base.Strings;
import qunar.tc.bistoury.instrument.client.metrics.Metrics;

import java.util.concurrent.TimeUnit;

/**
 * @author: leix.xie
 * @date: 2018/12/27 14:47
 * @describe：
 */
public class AgentMonitor {
    public static Long start() {
        return System.currentTimeMillis();
    }

    public static void stop(String key, long startTime) {
        if (startTime == 0L || Strings.isNullOrEmpty(key)) {
            return;
        }
        Metrics.counter(key + "_counter").delta().get().inc();
        Metrics.timer(key + "_timer").get().update(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
    }

    public static void exception(String key) {
        Metrics.counter(key + "_exception").delta().get().inc();
    }
}
