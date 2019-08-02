/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
