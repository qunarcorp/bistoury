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
import com.google.common.base.Strings;
import qunar.tc.bistoury.clientside.common.monitor.MetricsData;
import qunar.tc.bistoury.clientside.common.monitor.MetricsSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 抽象Processor监控指标汇报，通过Processor定义通用体。
 *
 * @since 16 September 2015
 */
public abstract class AbstractProcessorMetricsReportor implements MetricsReportor {
    private MetricProcessor processor;
    private Metrics metrics;

    public AbstractProcessorMetricsReportor(MetricProcessor processor, Metrics metrics) {
        this.processor = processor;
        this.metrics = metrics;
    }

    protected abstract void prepare(String name, MetricsSnapshot snapshot);

    protected void doStore(String name, MetricsSnapshot snapshot) {
        //默认打开所有指标
        // $name|type|tag|value
        List<MetricsData> metrics = new ArrayList<>();
        for (Map.Entry<MetricKey, Metric> e : this.metrics.metricCache.asMap().entrySet()) {
            MetricKey key = e.getKey();
            Metric value = e.getValue();
            if (!Strings.isNullOrEmpty(name) && !name.equals(key.name)) {
                continue;
            }
            MetricsData metricsData = processor.process(key, value);
            metrics.add(metricsData);
        }
        snapshot.setMetricsData(metrics);
    }

    @Override
    public MetricsSnapshot report(final String name) {
        MetricsSnapshot snapshot = new MetricsSnapshot();
        prepare(name, snapshot);
        doStore(name, snapshot);
        return snapshot;
    }


    interface MetricProcessor {
        MetricsData process(MetricKey key, Metric value);
    }

}
