package qunar.tc.bistoury.attach.arthas.profiler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenyu.nie created on 2019 2019/12/31 17:35
 */
public class GProfilerClients {

    private static volatile GProfilerClient client;

    private static AtomicBoolean init = new AtomicBoolean(false);

    public static GProfilerClient getInstance() {
        if (client != null) {
            return client;
        } else {
            throw new IllegalStateException("profiler client not available");
        }
    }

    public static GProfilerClient create() {
        if (init.compareAndSet(false, true)) {
            client = new GProfilerClient();
            return client;
        } else {
            throw new IllegalStateException("profiler client already created");
        }
    }
}
