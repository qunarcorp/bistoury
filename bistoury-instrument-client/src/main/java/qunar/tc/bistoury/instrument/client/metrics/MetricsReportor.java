package qunar.tc.bistoury.instrument.client.metrics;

import qunar.tc.bistoury.clientside.common.monitor.MetricsSnapshot;

/**
 * 监控指标汇报器。
 *
 * @author Daniel Li
 * @since 16 September 2015
 */
public interface MetricsReportor {

    MetricsSnapshot report(String name);
}