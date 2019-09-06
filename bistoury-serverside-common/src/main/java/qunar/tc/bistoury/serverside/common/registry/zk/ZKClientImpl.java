/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.serverside.common.registry.zk;

import com.google.common.collect.ImmutableList;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.serverside.common.registry.RegistryClient;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author leix.xie
 * @date 2019/7/4 18:28
 * @describe
 */
public class ZKClientImpl implements RegistryClient {

    private static final Logger logger = LoggerFactory.getLogger(ZKClientImpl.class);
    private final CuratorFramework client;
    private final String namespace;

    public ZKClientImpl(final String address, String namespace) {
        this.namespace = namespace;
        client = CuratorFrameworkFactory.builder()
                .connectString(address)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                .connectionTimeoutMs(5000).build();
        waitUntilZkStart();
        addPersistentNode(namespace);
    }

    @Override
    public void deleteNode(String node) throws Exception {
        try {
            client.delete()
                    .forPath(ZKPaths.makePath(namespace, node));
            logger.info("zk client remove node success. namespace: {}, node: {}", namespace, node);
        } catch (KeeperException.NoNodeException e) {
            //ignore
            logger.debug("nonode for namespace: {}, node: {}", namespace, node);
        }
    }

    @Override
    public List<String> getChildren() throws Exception {
        try {
            return client.getChildren().forPath(namespace);
        } catch (KeeperException.NoNodeException e) {
            //ignore
            logger.warn("nonode for namespace: {}", namespace);
            return ImmutableList.of();
        }
    }

    private void addPersistentNode(String path) {
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path);
            logger.info("zk client add namespace success. namespace: {}", namespace);
        } catch (KeeperException.NodeExistsException e) {
            //ignore
        } catch (Exception e) {
            throw new RuntimeException("addPersistentNode error", e);
        }
    }

    @Override
    public void addNode(String node) throws Exception {
        doAddEphemeralNode(node);
        addConnectionChangeListener((sender, state) -> {
            if (state == ConnectionState.RECONNECTED) {
                resetNode(node);
            }
        });
        logger.info("zk client add node success. namespace: {}, node: {}", namespace, node);
    }

    private void doAddEphemeralNode(String node) throws Exception {
        try {
            client.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(ZKPaths.makePath(namespace, node));
        } catch (KeeperException.NodeExistsException e) {
            logger.debug("Node already exists: {}", node);
        }
    }

    private void resetNode(String node) {
        try {
            deleteNode(node);
            doAddEphemeralNode(node);
        } catch (Exception e) {
            throw new RuntimeException("reset zk node error. node: " + node, e);
        }
    }

    private void addConnectionChangeListener(final ConnectionStateListener listener) {
        if (listener != null) {
            client.getConnectionStateListenable().addListener(listener);
        }
    }

    private void waitUntilZkStart() {
        final CountDownLatch latch = new CountDownLatch(1);
        addConnectionChangeListener((client, newState) -> {
            if (newState == ConnectionState.CONNECTED) {
                latch.countDown();
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

    @Override
    public void close() {
        client.close();
        logger.info("zk client close");
    }
}
