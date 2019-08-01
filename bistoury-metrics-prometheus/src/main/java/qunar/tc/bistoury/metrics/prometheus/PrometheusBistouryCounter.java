package qunar.tc.bistoury.metrics.prometheus;

import io.prometheus.client.Gauge;
import qunar.tc.bistoury.serverside.metrics.BistouryCounter;

/**
 * @author leix.xie
 * @date 2019/7/8 15:48
 * @describe
 */
public class PrometheusBistouryCounter implements BistouryCounter {
    private final Gauge.Child gauge;

    public PrometheusBistouryCounter(final Gauge gauge, final String[] labels) {
        this.gauge = gauge.labels(labels);
    }

    @Override
    public void inc() {
        gauge.inc();
    }

    @Override
    public void inc(final long n) {
        gauge.inc(n);
    }

    @Override
    public void dec() {
        gauge.dec();
    }

    @Override
    public void dec(final long n) {
        gauge.dec(n);
    }
}
