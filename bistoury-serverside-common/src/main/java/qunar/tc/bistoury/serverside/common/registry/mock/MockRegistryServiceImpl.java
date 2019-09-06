package qunar.tc.bistoury.serverside.common.registry.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.serverside.common.registry.RegistryClient;
import qunar.tc.bistoury.serverside.common.registry.RegistryClientHolder;
import qunar.tc.bistoury.serverside.common.registry.RegistryService;
import qunar.tc.bistoury.serverside.store.RegistryStore;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author cai.wen created on 2019/9/3 20:44
 */
public class MockRegistryServiceImpl implements RegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockRegistryServiceImpl.class);

    private RegistryClient mockClient;

    @Resource
    private RegistryStore registryStore;

    @PostConstruct
    public void init() {
        this.mockClient = RegistryClientHolder.getRegistryClient(registryStore);
    }

    @Override
    public void online() {
        try {
            mockClient.addNode("");
        } catch (Exception e) {
            throw new RuntimeException("mock online error.", e);
        }
    }

    @Override
    public void offline() {
        try {
            mockClient.deleteNode("");
        } catch (Exception e) {
            throw new RuntimeException("mock offline error.", e);
        }
    }

    @Override
    public List<String> getAllProxyUrls() {
        try {
            return mockClient.getChildren();
        } catch (Exception e) {
            throw new RuntimeException("mock getAllProxyUrls error.", e);
        }
    }
}
