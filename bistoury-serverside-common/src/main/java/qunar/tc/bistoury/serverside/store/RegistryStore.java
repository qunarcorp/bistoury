package qunar.tc.bistoury.serverside.store;

import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2018 2018/11/9 11:27
 */
public class RegistryStore {

    private static final String DEFAULT_ZK = "default";

    private static final String REGISTRY_CONFIG = "registry.properties";

    private String newBaseRoot = "/bistoury/proxy/new/group/";

    private String zkAddress;

    private String pathForNewUi;


    @PostConstruct
    public void init() {
        Map<String, String> registries = DynamicConfigLoader.load(REGISTRY_CONFIG).asMap();
        zkAddress = registries.get(DEFAULT_ZK);
        pathForNewUi = newBaseRoot + "ui";
    }


    public String getZkAddress() {
        return zkAddress;
    }

    public String getProxyZkPathForNewUi() {
        return pathForNewUi;
    }
}
