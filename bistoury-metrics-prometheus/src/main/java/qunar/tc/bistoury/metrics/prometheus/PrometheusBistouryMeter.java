package qunar.tc.bistoury.metrics.prometheus;

import io.prometheus.client.Summary;
import qunar.tc.bistoury.serverside.metrics.BistouryMeter;

/**
 * @author leix.xie
 * @date 2019/7/8 15:48
 * @describe
 */
public class PrometheusBistouryMeter implements BistouryMeter {
    private final Summary.Child summary;

    public PrometheusBistouryMeter(final Summary summary, final String[] labels) {
        this.summary = summary.labels(labels);
    }

    @Override
    public void mark() {
        summary.observe(1);
    }

    @Override
    public void mark(final long n) {
        summary.observe(n);
    }
}
