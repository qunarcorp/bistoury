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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leix.xie
 * @date 2019/7/4 18:28
 * @describe
 */
public class ZKClientImpl implements ZKClient {
    private static final Logger logger = LoggerFactory.getLogger(ZKClientImpl.class);
    private final AtomicInteger REFERENCE_COUNT = new AtomicInteger(0);
    private final CuratorFramework client;

    public ZKClientImpl(final String address) {
        client = CuratorFrameworkFactory.builder()
                .connectString(address)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                .connectionTimeoutMs(5000).build();
        waitUntilZkStart();
    }

    @Override
    public void deletePath(String path) throws Exception {
        client.delete().forPath(path);
    }

    @Override
    public List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    @Override
    public boolean checkExist(String path) {
        try {
            Stat stat = client.checkExists().forPath(path);
            return stat != null;
        } catch (Exception e) {
            logger.error("check exist error", e);
            return false;
        }
    }

    @Override
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

    @Override
    public String addEphemeralNode(String path) throws Exception {
        return client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
    }

    @Override
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

    @Override
    public void incrementReference() {
        REFERENCE_COUNT.incrementAndGet();
    }

    @Override
    public void close() {
        logger.info("Call close of ZKClient, reference count is: {}", REFERENCE_COUNT.get());
        if (REFERENCE_COUNT.decrementAndGet() == 0) {
            client.close();
            logger.info("zk client close");
        }
    }
}
