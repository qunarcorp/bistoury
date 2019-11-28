package qunar.tc.bistoury.attach.arthas.profiler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author cai.wen created on 2019/10/22 20:05
 */
public class ProfilerClients {

    private static volatile ProfilerClient client;

    private static AtomicBoolean init = new AtomicBoolean(false);

    public static ProfilerClient getInstance() {
        if (client != null) {
            return client;
        } else {
            throw new IllegalStateException("profiler client not available");
        }
    }

    public static ProfilerClient create() {
        if (init.compareAndSet(false, true)) {
            client = new ProfilerClient();
            return client;
        } else {
            throw new IllegalStateException("profiler client already created");
        }
    }
}
