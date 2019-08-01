package qunar.tc.bistoury.attach.arthas.debug;

import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: leix.xie
 * @date: 2019/2/28 10:51
 * @describe：
 */
public class JarDebugClients {
    private static volatile JarDebugClient client;
    private static AtomicBoolean init = new AtomicBoolean(false);

    public static JarDebugClient getInstance() {
        if (client != null) {
            return client;
        }
        throw new IllegalStateException("jar decompiler not available");
    }

    public static JarDebugClient create(InstrumentInfo instrumentInfo) {
        if (init.compareAndSet(false, true)) {
            client = new JarDebugClient(instrumentInfo);
            return client;
        } else {
            throw new IllegalStateException("jar decompiler client already created");
        }
    }
}
