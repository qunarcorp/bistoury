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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import qunar.tc.bistoury.clientside.common.monitor.MetricType;
import qunar.tc.bistoury.common.NamedThreadFactory;
import qunar.tc.bistoury.instrument.client.metrics.adapter.CounterAdapter;
import qunar.tc.bistoury.instrument.client.metrics.adapter.ResettableTimer;
import qunar.tc.bistoury.instrument.client.metrics.adapter.ResettableTimerAdapter;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author: leix.xie
 * @date: 2018/12/27 15:31
 * @describe：
 */
public class Metrics {

    public static final Metrics INSTANCE = new Metrics();

    public final Cache<MetricKey, Metric> metricCache = CacheBuilder.newBuilder().build();
    public final List<Delta> deltas = Lists.newCopyOnWriteArrayList();
    private long lastUpdate = 0;
    private Calendar calendar = Calendar.getInstance();

    private ScheduledExecutorService executor;

    Metrics() {
        initScheduler();
    }

    public static void destroy() {
        if (INSTANCE.executor != null) {
            INSTANCE.executor.shutdownNow();
        }
    }

    public static DeltaKeyWrapper<Counter> counter(final String name) {
        return new DeltaKeyWrapper<Counter>(name) {
            @Override
            public Counter get() {
                return new CounterAdapter(INSTANCE.getOrAdd(this, MetricBuilder.COUNTERS));
            }
        };
    }

    public static KeyWrapper<Timer> timer(final String name) {
        return new KeyWrapper<Timer>(name) {

            @Override
            public Timer get() {
                return new ResettableTimerAdapter(INSTANCE.getOrAdd(this, MetricBuilder.TIMERS));
            }
        };
    }

    public static MetricType typeOf(Metric metric) {
        if (metric instanceof com.codahale.metrics.Counter) {
            return MetricType.COUNTER;
        }
        if (metric instanceof ResettableTimer) {
            return MetricType.TIMER;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Metric> T getOrAdd(MetricKey key, MetricBuilder<T> builder) {
        final Metric metric = metricCache.getIfPresent(key);

        if (builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                boolean delta = false;
                boolean keep = false;
                if (DeltaKeyWrapper.class.isInstance(key)) {
                    DeltaKeyWrapper<T> _key = (DeltaKeyWrapper<T>) key;
                    delta = _key.delta;
                    keep = _key.keep;
                }
                return register(key, builder.newMetric(delta, keep));
            } catch (IllegalArgumentException e) {//被别人并发抢注了
                final Metric added = metricCache.getIfPresent(key);//这个地方是一定有值的，因为只有注册的方法，并没有移除的方法,上面出异常证明已经注册过了.
                if (builder.isInstance(added)) {
                    return (T) added;
                }
            }
        }

        throw new IllegalArgumentException(key + " is already used for a different type of metric");
    }

    <T extends Metric> T register(final MetricKey key, final T metric) throws IllegalArgumentException {
        try {
            return (T) metricCache.get(key, new Callable<Metric>() {
                @Override
                public Metric call() throws Exception {
                    if (Delta.class.isInstance(metric)) {
                        deltas.add((Delta) metric);
                    }
                    return metric;
                }
            });
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("fail to register metric,metric key:" + key);
        }

    }

    private void initScheduler() {
        executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("agent-metrics", true));
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long current = System.currentTimeMillis();
                if (current - lastUpdate < 50000L) {
                    return;
                }
                calendar.setTimeInMillis(current);
                if (calendar.get(Calendar.SECOND) > 10) {
                    return;
                }
                lastUpdate = current;
                for (Delta delta : deltas) {
                    delta.tick();
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    protected interface MetricBuilder<T extends Metric> {

        MetricBuilder<com.codahale.metrics.Counter> COUNTERS = new MetricBuilder<com.codahale.metrics.Counter>() {
            @Override
            public com.codahale.metrics.Counter newMetric(boolean delta, boolean keep) {
                return delta ? new DeltaCounter(keep) : new com.codahale.metrics.Counter();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return com.codahale.metrics.Counter.class.isInstance(metric);
            }
        };

        MetricBuilder<ResettableTimer> TIMERS = new MetricBuilder<ResettableTimer>() {
            @Override
            public ResettableTimer newMetric(boolean delta, boolean keep) {
                return new ResettableTimer();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return ResettableTimer.class.isInstance(metric);
            }
        };

        T newMetric(boolean delta, boolean keep);

        boolean isInstance(Metric metric);
    }
}
