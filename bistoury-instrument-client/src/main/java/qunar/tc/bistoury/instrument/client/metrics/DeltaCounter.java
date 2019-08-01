package qunar.tc.bistoury.instrument.client.metrics;

import java.util.concurrent.atomic.AtomicLong;

class DeltaCounter extends com.codahale.metrics.Counter implements Delta {

    // last, value
    private AtomicLong[] value = new AtomicLong[2];
    // keep value if delta=0?
    private final boolean keep;

    public DeltaCounter(boolean keep) {
        this.keep = keep;
        value[0] = new AtomicLong();
        value[1] = new AtomicLong();
    }

    @Override
    public void tick() {
        long cur_cnt = super.getCount();
        long last_cnt = value[0].get();

        long delta = cur_cnt - last_cnt;
        if (keep && delta == 0) {
            return;
        }
        value[0].set(cur_cnt);
        value[1].set(delta);
    }

    @Override
    public long getCount() {
        return value[1].get();
    }
}
