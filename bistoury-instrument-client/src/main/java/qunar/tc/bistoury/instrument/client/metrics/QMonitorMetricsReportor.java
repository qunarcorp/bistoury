package qunar.tc.bistoury.instrument.client.metrics;


import com.codahale.metrics.Metric;
import qunar.tc.bistoury.clientside.common.monitor.MetricType;
import qunar.tc.bistoury.clientside.common.monitor.MetricsData;
import qunar.tc.bistoury.clientside.common.monitor.MetricsSnapshot;
import qunar.tc.bistoury.common.DateUtil;

/**
 * @author: leix.xie
 * @date: 2018/12/27 20:20
 * @describe：
 */
public class QMonitorMetricsReportor extends AbstractProcessorMetricsReportor {

    public QMonitorMetricsReportor(Metrics metrics) {
        super(DEFAULT_PROCESSOR, metrics);
    }

    @Override
    protected void prepare(String name, MetricsSnapshot snapshot) {
        snapshot.setTimestamp(DateUtil.getMinute());
    }

    static final MetricProcessor DEFAULT_PROCESSOR = new MetricProcessor() {

        @Override
        public MetricsData process(MetricKey key, Metric value) {
            MetricType type = Metrics.typeOf(value);
            float[] data = ItemValue.valueOf(type, value);
            MetricsData snapshot = new MetricsData();
            snapshot.setName(key.name);
            snapshot.setType(type.code());
            snapshot.setData(data);
            return snapshot;
        }
    };
}
