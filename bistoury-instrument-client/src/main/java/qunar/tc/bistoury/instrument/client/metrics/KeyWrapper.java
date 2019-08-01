package qunar.tc.bistoury.instrument.client.metrics;

public abstract class KeyWrapper<T> extends MetricKey {

    public KeyWrapper(String name) {
        super(name);
    }

    public abstract T get();
}