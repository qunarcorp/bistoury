package qunar.tc.bistoury.serverside.common.registry.zk;

import com.google.common.collect.ImmutableList;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.serverside.agile.Conf;
import qunar.tc.bistoury.serverside.agile.LocalHost;
import qunar.tc.bistoury.serverside.common.registry.RegistryService;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.store.RegistryStore;
import qunar.tc.bistoury.serverside.util.ServerManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author cai.wen created on 2019/9/2 15:45
 */
public class ZkRegistryServiceImpl implements RegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkRegistryServiceImpl.class);

    private ZKClient zkClient;

    @Resource
    private RegistryStore registryStore;

    @PostConstruct
    public void init() {
        this.zkClient = ZKClientCache.get(registryStore.getZkAddress());
    }

    @Override
    public void online() {
        deletePath(getProxyNode());
        doOnline();
    }

    private void doOnline() {
        ZKPaths.PathAndNode proxyNode = getProxyNode();
        String fullPath = ZKPaths.makePath(proxyNode.getPath(), proxyNode.getNode());
        try {
            zkClient.addPersistentNode(proxyNode.getPath());
            zkClient.addEphemeralNode(fullPath);
        } catch (Exception e) {
            LOGGER.error("online proxy error. path: {}", fullPath, e);
        }
    }

    @Override
    public void offline() {
        deletePath(getProxyNode());
    }

    @Override
    public List<String> getAllProxyUrls() {
        try {
            return zkClient.getChildren(registryStore.getProxyZkPathForNewUi());
        } catch (Exception e) {
            LOGGER.error("get all proxy urls error.", e);
            return ImmutableList.of();
        }
    }

    private void deletePath(ZKPaths.PathAndNode pathAndNode) {
        String path = ZKPaths.makePath(pathAndNode.getPath(), pathAndNode.getNode());
        try {
            zkClient.deletePath(path);
            LOGGER.info("zk delete successfully, path {}", path);
        } catch (KeeperException.NoNodeException e) {
            // ignore
        } catch (Exception e) {
            LOGGER.error("zk delete path: {} error", path, e);
            throw new RuntimeException("zk delete path: " + path + " error", e);
        }
    }


    private int websocketPort = -1;
    private int tomcatPort = -1;

    private ZKPaths.PathAndNode getProxyNode() {
        initProxyPort();
        String node = LocalHost.getLocalHost() + ":" + tomcatPort + ":" + websocketPort;
        return new ZKPaths.PathAndNode(registryStore.getProxyZkPathForNewUi(), node);
    }

    private synchronized void initProxyPort() {
        if (tomcatPort == -1) {
            Conf conf = Conf.fromMap(DynamicConfigLoader.load("global.properties").asMap());
            websocketPort = conf.getInt("server.port", -1);
            tomcatPort = ServerManager.getTomcatPort();
        }
    }
}
