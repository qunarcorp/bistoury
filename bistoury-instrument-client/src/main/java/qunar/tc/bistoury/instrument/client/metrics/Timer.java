package qunar.tc.bistoury.instrument.client.metrics;

import java.util.concurrent.TimeUnit;

public interface Timer {

    void update(long duration, TimeUnit unit);

    Context time();

    Context time(long startTime);

    interface Context {
        long directGet();
        long stop();
    }
}
