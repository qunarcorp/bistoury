package qunar.tc.bistoury.serverside.metrics;

import com.google.common.base.Supplier;

import java.util.ServiceLoader;

/**
 * @author leix.xie
 * @date 2019/7/8 15:59
 * @describe
 */
public class Metrics {
    private static final String[] EMPTY = new String[0];
    private static final BistouryMetricRegistry INSTANCE;

    static {
        ServiceLoader<BistouryMetricRegistry> registries = ServiceLoader.load(BistouryMetricRegistry.class);
        BistouryMetricRegistry instance = null;
        for (BistouryMetricRegistry registry : registries) {
            instance = registry;
        }
        if (instance == null) {
            instance = new MockRegistry();
        }
        INSTANCE = instance;
    }

    public static void gauge(String name, String[] tags, String[] values, Supplier<Double> supplier) {
        INSTANCE.newGauge(name, tags, values, supplier);
    }

    public static void gauge(String name, Supplier<Double> supplier) {
        INSTANCE.newGauge(name, EMPTY, EMPTY, supplier);
    }

    public static BistouryCounter counter(String name, String[] tags, String[] values) {
        return INSTANCE.newCounter(name, tags, values);
    }

    public static BistouryCounter counter(String name) {
        return INSTANCE.newCounter(name, EMPTY, EMPTY);
    }

    public static BistouryMeter meter(String name, String[] tags, String[] values) {
        return INSTANCE.newMeter(name, tags, values);
    }

    public static BistouryMeter meter(String name) {
        return INSTANCE.newMeter(name, EMPTY, EMPTY);
    }

    public static BistouryTimer timer(String name, String[] tags, String[] values) {
        return INSTANCE.newTimer(name, tags, values);
    }

    public static BistouryTimer timer(String name) {
        return INSTANCE.newTimer(name, EMPTY, EMPTY);
    }

    public static void remove(String name, String[] tags, String[] values) {
        INSTANCE.remove(name, tags, values);
    }
}
