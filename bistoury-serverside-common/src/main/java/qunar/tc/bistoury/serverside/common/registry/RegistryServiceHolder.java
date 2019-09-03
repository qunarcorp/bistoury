package qunar.tc.bistoury.serverside.common.registry;

import qunar.tc.bistoury.serverside.common.registry.etcd2.EtcdV2RegistryServiceImpl;
import qunar.tc.bistoury.serverside.common.registry.zk.ZkRegistryServiceImpl;

/**
 * @author cai.wen created on 2019/9/2 18:03
 */
public class RegistryServiceHolder {

    private static RegistryService INSTANCE = null;

    private static RegistryType registryType;

    public static synchronized RegistryService getRegistryService(RegistryType registryType) {
        checkRegistryType(registryType);
        RegistryServiceHolder.registryType = registryType;

        if (INSTANCE == null) {
            switch (registryType) {
                case ZOOKEEPER:
                    INSTANCE = new ZkRegistryServiceImpl();
                    break;
                case ETCD_V2:
                    INSTANCE = new EtcdV2RegistryServiceImpl();
                    break;
                default:
                    throw new IllegalStateException("请指定相应的注册中心类型.");
            }
        }
        return INSTANCE;
    }

    private static void checkRegistryType(RegistryType registryType) {
        if (RegistryServiceHolder.registryType != null && registryType != RegistryServiceHolder.registryType) {
            throw new IllegalStateException("不可以指定多种注册中心");
        }
    }
}
