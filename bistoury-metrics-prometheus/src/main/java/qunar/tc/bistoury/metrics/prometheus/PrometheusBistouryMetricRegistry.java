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

package qunar.tc.bistoury.metrics.prometheus;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.prometheus.client.*;
import io.prometheus.client.bridge.Graphite;
import io.prometheus.client.exporter.HTTPServer;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;
import qunar.tc.bistoury.serverside.metrics.BistouryCounter;
import qunar.tc.bistoury.serverside.metrics.BistouryMeter;
import qunar.tc.bistoury.serverside.metrics.BistouryMetricRegistry;
import qunar.tc.bistoury.serverside.metrics.BistouryTimer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author leix.xie
 * @date 2019/7/8 15:46
 * @describe
 */
public class PrometheusBistouryMetricRegistry implements BistouryMetricRegistry {

    private static final LoadingCache<Key, Collector> CACHE = CacheBuilder.newBuilder()
            .build(new CacheLoader<Key, Collector>() {
                @Override
                public Collector load(Key key) {
                    return key.create();
                }
            });


    public PrometheusBistouryMetricRegistry() {
        DynamicConfig<LocalDynamicConfig> config = DynamicConfigLoader.load("prometheus.properties", false);
        String type = config.getString("monitor.type", "prometheus");
        if ("prometheus".equals(type)) {
            String action = config.getString("monitor.action", "metrics");
            if ("metrics".equals(action)) {
                try {
                    HTTPServer server = new HTTPServer(config.getInt("monitor.port", 3333));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if ("graphite".equals(type)) {
            String host = config.getString("graphite.host");
            int port = config.getInt("graphite.port");
            Graphite graphite = new Graphite(host, port);
            graphite.start(CollectorRegistry.defaultRegistry, 60);
        }
    }

    @SuppressWarnings("unchecked")
    private static <M extends Collector> M cacheFor(Key<M> key) {
        return (M) CACHE.getUnchecked(key);
    }

    @Override
    public void newGauge(String name, String[] tags, String[] values, Supplier<Double> supplier) {
        final PrometheusBistouryGauge gauge = cacheFor(new GuageKey(name, tags));
        gauge.labels(values).setSupplier(supplier);
    }

    @Override
    public BistouryCounter newCounter(String name, String[] tags, String[] values) {
        final Gauge gauge = cacheFor(new CounterKey(name, tags));
        return new PrometheusBistouryCounter(gauge, values);
    }

    @Override
    public BistouryMeter newMeter(String name, String[] tags, String[] values) {
        final Summary summary = cacheFor(new MeterKey(name, tags));
        return new PrometheusBistouryMeter(summary, values);
    }

    @Override
    public BistouryTimer newTimer(String name, String[] tags, String[] values) {
        final Summary summary = cacheFor(new TimerKey(name, tags));
        return new PrometheusBistouryTimer(summary, values);
    }

    @Override
    public void remove(String name, String[] tags, String[] values) {
        final Collector collector = CACHE.getIfPresent(new SimpleCollectorKey(name, tags));
        if (collector == null) return;
        CollectorRegistry.defaultRegistry.unregister(collector);
    }

    private static abstract class Key<M extends Collector> {
        final String name;
        final String[] tags;

        Key(String name, String[] tags) {
            this.name = name;
            this.tags = tags;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Key key = (Key) o;

            if (!Objects.equals(name, key.name))
                return false;
            return Arrays.equals(tags, key.tags);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (tags != null ? Arrays.hashCode(tags) : 0);
            return result;
        }

        public abstract M create();
    }

    private static class SimpleCollectorKey extends Key<SimpleCollector> {

        SimpleCollectorKey(final String name, final String[] tags) {
            super(name, tags);
        }

        @Override
        public SimpleCollector create() {
            return null;
        }
    }

    private static class GuageKey extends Key<PrometheusBistouryGauge> {
        GuageKey(final String name, final String[] tags) {
            super(name, tags);
        }

        @Override
        public PrometheusBistouryGauge create() {
            return PrometheusBistouryGauge.build().name(name).help(name).labelNames(tags).create().register();
        }
    }

    private static class CounterKey extends Key<Gauge> {
        CounterKey(final String name, final String[] tags) {
            super(name, tags);
        }

        @Override
        public Gauge create() {
            return Gauge.build().name(name).help(name).labelNames(tags).create().register();
        }
    }

    private static class MeterKey extends Key<Summary> {

        MeterKey(final String name, final String[] tags) {
            super(name, tags);
        }

        @Override
        public Summary create() {
            return Summary.build().name(name).help(name).labelNames(tags).create().register();
        }
    }

    private static class TimerKey extends Key<Summary> {

        TimerKey(final String name, final String[] tags) {
            super(name, tags);
        }

        @Override
        public Summary create() {
            return Summary.build()
                    .name(name)
                    .help(name)
                    .labelNames(tags)
                    .quantile(0.5, 0.05)
                    .quantile(0.75, 0.05)
                    .quantile(0.99, 0.05)
                    .create()
                    .register();
        }
    }
}
