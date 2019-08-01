package qunar.tc.bistoury.serverside.metrics;

import com.google.common.base.Supplier;

/**
 * @author leix.xie
 * @date 2019/7/8 15:10
 * @describe
 */
public interface BistouryMetricRegistry {
    void newGauge(final String name, final String[] tags, final String[] values, final Supplier<Double> supplier);

    BistouryCounter newCounter(final String name, final String[] tags, final String[] values);

    BistouryMeter newMeter(final String name, final String[] tags, final String[] values);

    BistouryTimer newTimer(final String name, final String[] tags, final String[] values);

    void remove(final String name, final String[] tags, final String[] values);
}
