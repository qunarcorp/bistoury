package qunar.tc.bistoury.serverside.common;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leix.xie
 * @date 2019/7/4 18:28
 * @describe
 */
public class ZKClient {
    private static final Logger logger = LoggerFactory.getLogger(ZKClient.class);
    private final AtomicInteger REFERENCE_COUNT = new AtomicInteger(0);
    private final CuratorFramework client;

    public ZKClient(final String address) {
        client = CuratorFrameworkFactory.builder()
                .connectString(address)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                .connectionTimeoutMs(5000).build();
        waitUntilZkStart();
    }

    public void deletePath(String path) throws Exception {
        client.delete().forPath(path);
    }

    public List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public boolean checkExist(String path) {
        try {
            Stat stat = client.checkExists().forPath(path);
            return stat != null;
        } catch (Exception e) {
            logger.error("check exist error", e);
            return false;
        }
    }

    public void addPersistentNode(String path) throws Exception {
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path);
        } catch (KeeperException.NodeExistsException e) {
            logger.warn("Node already exists: {}", path);
        } catch (Exception e) {
            throw new Exception("addPersistentNode error", e);
        }
    }

    public String addEphemeralNode(String path) throws Exception {
        return client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
    }

    public void addConnectionChangeListener(final ConnectionStateListener listener) {
        if (listener != null) {
            client.getConnectionStateListenable().addListener(listener);
        }
    }

    private void waitUntilZkStart() {
        final CountDownLatch latch = new CountDownLatch(1);
        addConnectionChangeListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.CONNECTED) {
                    latch.countDown();
                }
            }
        });
        client.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("start zk latch.await() error", e);
            Thread.currentThread().interrupt();
        }
    }

    protected void incrementReference() {
        REFERENCE_COUNT.incrementAndGet();
    }

    @PreDestroy
    public void close() {
        logger.info("Call close of ZKClient, reference count is: {}", REFERENCE_COUNT.get());
        if (REFERENCE_COUNT.decrementAndGet() == 0) {
            client.close();
            logger.info("zk client close");
        }
    }
}
