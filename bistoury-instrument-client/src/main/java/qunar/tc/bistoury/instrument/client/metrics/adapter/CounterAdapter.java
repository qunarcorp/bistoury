package qunar.tc.bistoury.instrument.client.metrics.adapter;


import qunar.tc.bistoury.instrument.client.metrics.Counter;

public class CounterAdapter implements Counter {

    protected final com.codahale.metrics.Counter _counter;

    public CounterAdapter(com.codahale.metrics.Counter counter) {
        this._counter = counter;
    }

    @Override
    public void inc() {
        inc(1);
    }

    @Override
    public void inc(long n) {
        _counter.inc(n);
    }

    @Override
    public void dec() {
        dec(1);
    }

    @Override
    public void dec(long n) {
        _counter.dec(n);
    }

    @Override
    public long getCount() {
        return _counter.getCount();
    }
}