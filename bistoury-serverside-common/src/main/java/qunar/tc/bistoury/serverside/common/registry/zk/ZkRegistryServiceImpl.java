package qunar.tc.bistoury.serverside.common.registry.zk;

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
 * @author cai.wen created on 2019/9/2 15:45
 */
public class ZkRegistryServiceImpl implements RegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkRegistryServiceImpl.class);

    private RegistryClient registryClient;

    @Resource
    private RegistryStore registryStore;

    @PostConstruct
    public void init() {
        this.registryClient = RegistryClientHolder.getRegistryClient(registryStore);
    }

    @Override
    public void online() {
        deleteNode(GlobalConfigStore.getProxyNode());
        doOnline();
    }

    private void doOnline() {
        String node = GlobalConfigStore.getProxyNode();
        try {
            registryClient.addEphemeralNode(node);
        } catch (Exception e) {
            LOGGER.error("online proxy error. node: {}", node, e);
        }
    }

    @Override
    public void offline() {
        deleteNode(GlobalConfigStore.getProxyNode());
    }

    @Override
    public List<String> getAllProxyUrls() {
        try {
            return registryClient.getChildren();
        } catch (Exception e) {
            LOGGER.error("get all proxy urls error.", e);
            return ImmutableList.of();
        }
    }

    private void deleteNode(String node) {
        try {
            registryClient.deleteNode(node);
            LOGGER.info("zk delete successfully, node {}", node);
        } catch (Exception e) {
            LOGGER.error("zk delete node: {} error", node, e);
            throw new RuntimeException("zk delete node: " + node + " error", e);
        }
    }

}
