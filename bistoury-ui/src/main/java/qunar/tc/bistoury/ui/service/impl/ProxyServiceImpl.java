package qunar.tc.bistoury.ui.service.impl;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.common.ZKClient;
import qunar.tc.bistoury.serverside.common.ZKClientCache;
import qunar.tc.bistoury.serverside.store.RegistryStore;
import qunar.tc.bistoury.ui.service.ProxyService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Service
public class ProxyServiceImpl implements ProxyService {

    private static final Logger logger = LoggerFactory.getLogger(ProxyServiceImpl.class);

    @Resource
    private RegistryStore registryStore;

    private ZKClient zkClient;

    @PostConstruct
    public void init() {
        zkClient = ZKClientCache.get(registryStore.getZkAddress());
    }

    @Override
    public List<String> getAllProxyUrls() {
        try {
            return zkClient.getChildren(registryStore.getProxyZkPathForNewUi());
        } catch (Exception e) {
            logger.error("get all proxy server address error", e);
            return ImmutableList.of();
        }
    }
}
