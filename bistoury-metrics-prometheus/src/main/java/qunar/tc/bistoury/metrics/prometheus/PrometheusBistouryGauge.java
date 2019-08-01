package qunar.tc.bistoury.metrics.prometheus;

import com.google.common.base.Supplier;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import io.prometheus.client.SimpleCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leix.xie
 * @date 2019/7/8 15:47
 * @describe
 */
public class PrometheusBistouryGauge extends SimpleCollector<PrometheusBistouryGauge.Child> implements Collector.Describable {

    PrometheusBistouryGauge(Builder b) {
        super(b);
    }

    public static Builder build() {
        return new Builder();
    }

    @Override
    protected Child newChild() {
        return new Child();
    }

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples.Sample> samples = new ArrayList<>(children.size());
        for (Map.Entry<List<String>, Child> c : children.entrySet()) {
            samples.add(new MetricFamilySamples.Sample(fullname, labelNames, c.getKey(), c.getValue().get()));
        }
        return familySamplesList(Type.GAUGE, samples);
    }

    @Override
    public List<MetricFamilySamples> describe() {
        List<MetricFamilySamples> list = new ArrayList<>();
        list.add(new GaugeMetricFamily(fullname, help, labelNames));
        return list;
    }

    public static class Builder extends SimpleCollector.Builder<Builder, PrometheusBistouryGauge> {
        @Override
        public PrometheusBistouryGauge create() {
            return new PrometheusBistouryGauge(this);
        }
    }

    public static class Child {
        private Supplier<Double> supplier;

        public void setSupplier(final Supplier<Double> supplier) {
            this.supplier = supplier;
        }

        public double get() {
            if (supplier == null) {
                return 0;
            } else {
                return supplier.get();
            }
        }
    }
}