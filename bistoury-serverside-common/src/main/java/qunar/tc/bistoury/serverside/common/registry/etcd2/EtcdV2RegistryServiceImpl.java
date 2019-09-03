package qunar.tc.bistoury.serverside.common.registry.etcd2;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.serverside.common.registry.RegistryClient;
import qunar.tc.bistoury.serverside.common.registry.RegistryClientHolder;
import qunar.tc.bistoury.serverside.common.registry.RegistryService;
import qunar.tc.bistoury.serverside.store.GlobalConfigStore;
import qunar.tc.bistoury.serverside.store.RegistryStore;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author cai.wen created on 2019/9/2 17:45
 */
public class EtcdV2RegistryServiceImpl implements RegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdV2RegistryServiceImpl.class);

    private RegistryClient etcdV2Client;

    @Resource
    private RegistryStore registryStore;

    @PostConstruct
    public void init() {
        this.etcdV2Client = RegistryClientHolder.getRegistryClient(registryStore);
    }

    @Override
    public void online() {
        try {
            etcdV2Client.addEphemeralNode(GlobalConfigStore.getProxyNode());
        } catch (Exception e) {
            throw new RuntimeException("online proxy error.", e);
        }
    }

    @Override
    public void offline() {
        try {
            etcdV2Client.deleteNode(GlobalConfigStore.getProxyNode());
        } catch (Exception e) {
            throw new RuntimeException("offline proxy error.", e);
        }
    }

    @Override
    public List<String> getAllProxyUrls() {
        try {
            return etcdV2Client.getChildren();
        } catch (Exception e) {
            LOGGER.error("get all proxys error.", e);
            return ImmutableList.of();
        }
    }

}
