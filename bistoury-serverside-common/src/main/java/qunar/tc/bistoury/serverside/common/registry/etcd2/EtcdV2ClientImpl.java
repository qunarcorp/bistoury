package qunar.tc.bistoury.serverside.common.registry.etcd2;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import mousio.client.retry.RetryWithTimeout;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.NamedThreadFactory;
import qunar.tc.bistoury.serverside.common.registry.RegistryClient;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author cai.wen created on 2019/9/3 15:47
 */
public class EtcdV2ClientImpl implements RegistryClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdV2ClientImpl.class);

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("bistoury-registry-etcd-v2"));

    private volatile EtcdClient etcdClient;

    private final List<URI> etcdServerUrls;

    private final String namespace;

    private final Set<String> proxys = Sets.newConcurrentHashSet();

    private static final int ttlSec = 15;

    private static final int leaseSec = 10;

    private static final int timeoutInMs = 5000;

    public EtcdV2ClientImpl(List<String> uris, String namespace) {
        this.namespace = namespace;
        this.etcdServerUrls = initUrl(uris);
        resetClient();
        initLeaseKeepAlive();
    }

    private List<URI> initUrl(List<String> uris) {
        return uris.stream()
                .map(URI::create)
                .collect(Collectors.toList());
    }

    private void resetClient() {
        close();
        URI[] etcdServerArrays = new URI[etcdServerUrls.size()];
        etcdServerUrls.toArray(etcdServerArrays);
        etcdClient = new EtcdClient(etcdServerArrays);
    }

    private void initLeaseKeepAlive() {
        scheduledExecutorService.scheduleAtFixedRate(this::runLeaseKeepAlive, 0, leaseSec, TimeUnit.SECONDS);
    }

    @Override
    public void deleteNode(String node) {
        proxys.remove(node);
        runLeaseKeepAlive();
        LOGGER.info("etcd client remove node. namespace: {}, node: {}", namespace, node);
    }

    @Override
    public void addNode(String node) {
        proxys.add(node);
        runLeaseKeepAlive();
        LOGGER.info("etcd client add node. namespace: {}, node: {}", namespace, node);
    }

    @Override
    public List<String> getChildren() {
        try {
            EtcdKeysResponse.EtcdNode etcdNode = etcdClient.get(namespace)
                    .setRetryPolicy(new RetryWithTimeout(200, timeoutInMs))
                    .dir().send().get().node;
            return getNodeChildren(etcdNode);
        } catch (Exception e) {
            throw new RuntimeException("etcd client getChildren error.", e);
        }
    }

    private List<String> getNodeChildren(EtcdKeysResponse.EtcdNode etcdNode) {
        return etcdNode.getNodes().stream()
                .map(EtcdKeysResponse.EtcdNode::getValue)
                .collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableList::copyOf));
    }

    @Override
    public void close() {
        if (etcdClient != null) {
            try {
                etcdClient.close();
                LOGGER.info("close etcd client success. namespace: {}. nodes: {}.", namespace, proxys);
            } catch (IOException e) {
                throw new RuntimeException("close etcd v2 client error.", e);
            }
        }
    }

    private void runLeaseKeepAlive() {
        for (String proxyNode : proxys) {
            try {
                etcdClient.post(namespace, proxyNode)
                        .setRetryPolicy(new RetryWithTimeout(200, timeoutInMs))
                        .ttl(ttlSec).send().get();
            } catch (Exception e) {
                LOGGER.error("lease keep alive error. namespace: {}, proxyNode:{}.", namespace, proxyNode, e);
            }
        }
    }
}
