package qunar.tc.bistoury.attach.arthas.debug;

import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenyu.nie created on 2019 2019/2/18 15:58
 */
public class QDebugClients {

    private static volatile QDebugClient client;

    private static AtomicBoolean init = new AtomicBoolean(false);

    public static QDebugClient getInstance() {
        if (client != null) {
            return client;
        } else {
            throw new IllegalStateException("qdebug client not available");
        }
    }

    public static QDebugClient create(InstrumentInfo instrumentInfo) {
        if (init.compareAndSet(false, true)) {
            client = new QDebugClient(instrumentInfo);
            return client;
        } else {
            throw new IllegalStateException("qdebug client already created");
        }
    }
}
