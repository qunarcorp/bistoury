package qunar.tc.bistoury.metrics.prometheus;

import io.prometheus.client.Summary;
import qunar.tc.bistoury.serverside.metrics.BistouryTimer;

import java.util.concurrent.TimeUnit;

/**
 * @author leix.xie
 * @date 2019/7/8 15:48
 * @describe
 */
public class PrometheusBistouryTimer implements BistouryTimer {
    private final Summary.Child summary;

    public PrometheusBistouryTimer(final Summary summary, final String[] labels) {
        this.summary = summary.labels(labels);
    }

    @Override
    public void update(final long duration, final TimeUnit unit) {
        summary.observe(unit.toMillis(duration));
    }
}
