package qunar.tc.bistoury.attach.arthas.jar;

import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: leix.xie
 * @date: 2019/2/25 16:40
 * @describe：
 */
public class JarInfoClients {
    private static volatile JarInfoClient client;

    private static AtomicBoolean init = new AtomicBoolean(false);

    public static JarInfoClient getInstance() {
        if (client != null) {
            return client;
        } else {
            throw new IllegalStateException("jar info client not available");
        }
    }

    public static JarInfoClient create(InstrumentInfo instrumentInfo) {
        if (init.compareAndSet(false, true)) {
            client = new JarInfoClient(instrumentInfo);
            return client;
        } else {
            throw new IllegalStateException("jar info client already created");
        }
    }
}
