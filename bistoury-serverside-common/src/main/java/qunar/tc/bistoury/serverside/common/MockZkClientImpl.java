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

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019-08-09 12:06
 * @describe
 */
public class MockZkClientImpl implements ZKClient {
    private static final Logger logger = LoggerFactory.getLogger(MockZkClientImpl.class);

    private String zkChildrenPath;

    public MockZkClientImpl(final String zkChildrenPath) {
        this.zkChildrenPath = zkChildrenPath;
    }

    @Override
    public void deletePath(String path) throws Exception {
        logger.info("zk mock\t delete path, path: {}", path);
    }

    @Override
    public List<String> getChildren(String path) throws Exception {
        List<String> list = Files.readLines(new File(zkChildrenPath), Charsets.UTF_8);
        logger.info("zk mock\t get children, path:{}, children: {}", path, list);
        return ImmutableList.copyOf(list);
    }

    @Override
    public boolean checkExist(String path) {
        logger.info("zk mock\t check exist, path: {}", path);
        return true;
    }

    @Override
    public void addPersistentNode(String path) throws Exception {
        logger.info("zk mock\t add persistent node, path: {}", path);
    }

    @Override
    public String addEphemeralNode(String path) throws Exception {
        logger.info("zk mock\t add ephemeral node, path: {}", path);
        return path;
    }

    @Override
    public void addConnectionChangeListener(ConnectionStateListener listener) {
        logger.info("zk mock\t add connection change listener");
    }

    @Override
    public void incrementReference() {
        logger.info("zk mock\t increment reference");
    }

    @Override
    public void close() {
        logger.info("zk mock\t close zk client");
    }
}
