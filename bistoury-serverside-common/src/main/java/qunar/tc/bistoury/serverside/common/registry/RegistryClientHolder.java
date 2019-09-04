package qunar.tc.bistoury.serverside.common.registry;

import qunar.tc.bistoury.serverside.common.registry.etcd2.EtcdV2ClientImpl;
import qunar.tc.bistoury.serverside.common.registry.mock.MockRegistryClient;
import qunar.tc.bistoury.serverside.common.registry.zk.ZKClientImpl;
import qunar.tc.bistoury.serverside.store.RegistryStore;

/**
 * @author cai.wen created on 2019/9/3 18:25
 */
public class RegistryClientHolder {

    private static RegistryClient INSTANCE;

    private static RegistryType registryType;

    public synchronized static RegistryClient getRegistryClient(RegistryStore registryStore) {
        checkRegistryType(registryStore.getRegistryType());
        registryType = registryStore.getRegistryType();

        if (INSTANCE == null) {
            switch (registryType) {
                case ETCD_V2:
                    INSTANCE = new EtcdV2ClientImpl(registryStore.getEtcdUris(), registryStore.getProxyZkPathForNewUi());
                    break;
                case ZOOKEEPER:
                    INSTANCE = new ZKClientImpl(registryStore.getZkAddress(), registryStore.getProxyZkPathForNewUi());
                    break;
                case MOCK:
                    INSTANCE = new MockRegistryClient(registryStore.getLocalZkTagFile());
                    break;
                default:
                    throw new IllegalStateException("请指定相应的注册中心类型.");
            }
        }
        return INSTANCE;
    }

    private static void checkRegistryType(RegistryType registryType) {
        if (RegistryClientHolder.registryType != null && registryType != RegistryClientHolder.registryType) {
            throw new IllegalStateException("不可以指定多种注册中心");
        }
    }
}
