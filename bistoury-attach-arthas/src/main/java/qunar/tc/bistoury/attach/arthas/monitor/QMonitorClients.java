package qunar.tc.bistoury.attach.arthas.monitor;

import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenyu.nie created on 2019 2019/2/18 16:13
 */
public class QMonitorClients {

    private static volatile QMonitorClient client;

    private static AtomicBoolean init = new AtomicBoolean(false);

    public static QMonitorClient getInstance() {
        if (client != null) {
            return client;
        } else {
            throw new IllegalStateException("qdebug client not available");
        }
    }

    public static QMonitorClient create(InstrumentInfo instrumentInfo) {
        if (init.compareAndSet(false, true)) {
            client = new QMonitorClient(instrumentInfo);
            return client;
        } else {
            throw new IllegalStateException("qdebug client already created");
        }
    }
}
