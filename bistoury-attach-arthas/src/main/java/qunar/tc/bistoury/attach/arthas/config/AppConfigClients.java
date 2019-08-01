package qunar.tc.bistoury.attach.arthas.config;

import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: leix.xie
 * @date: 2019/3/4 20:58
 * @describe：
 */
public class AppConfigClients {
    private static volatile AppConfigClient client;
    private static AtomicBoolean init = new AtomicBoolean(false);

    public static AppConfigClient getInstance() {
        if (client != null) {
            return client;
        } else {
            throw new IllegalStateException("app config client not available");
        }
    }

    public static AppConfigClient create(InstrumentInfo instrumentInfo) {
        if (init.compareAndSet(false, true)) {
            client = new AppConfigClient(instrumentInfo);
            return client;
        } else {
            throw new IllegalStateException("app config client already created");
        }
    }
}
