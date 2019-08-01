
package qunar.tc.bistoury.serverside.configuration.local;

import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author keli.wang
 * @since 2018-11-27
 */
public class LocalDynamicConfigFactory implements DynamicConfigFactory {
    private final ConfigWatcher watcher = new ConfigWatcher();
    private final ConcurrentMap<String, LocalDynamicConfig> configs = new ConcurrentHashMap<>();

    @Override
    public DynamicConfig create(final String name, final boolean failOnNotExist) {
        if (configs.containsKey(name)) {
            return configs.get(name);
        }

        return doCreate(name, failOnNotExist);
    }

    private LocalDynamicConfig doCreate(final String name, final boolean failOnNotExist) {
        final LocalDynamicConfig prev = configs.putIfAbsent(name, new LocalDynamicConfig(name, failOnNotExist));
        final LocalDynamicConfig config = configs.get(name);
        if (prev == null) {
            watcher.addWatch(config);
            config.onConfigModified();
        }
        return config;
    }
}
