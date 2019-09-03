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

package qunar.tc.bistoury.serverside.common.registry.mock;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.serverside.common.registry.RegistryClient;

import java.io.File;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019-08-09 12:06
 * @describe
 */
public class MockRegistryClient implements RegistryClient {
    private static final Logger logger = LoggerFactory.getLogger(MockRegistryClient.class);

    private String zkChildrenPath;

    public MockRegistryClient(final String zkChildrenPath) {
        this.zkChildrenPath = zkChildrenPath;
    }

    @Override
    public void deleteNode(String node) throws Exception {
        logger.info("zk mock\t delete node, node: {}", node);
    }

    @Override
    public List<String> getChildren() throws Exception {
        List<String> list = Files.readLines(new File(zkChildrenPath), Charsets.UTF_8);
        logger.info("zk mock\t get children, children: {}", list);
        return ImmutableList.copyOf(list);
    }

    @Override
    public void addEphemeralNode(String node) throws Exception {
        logger.info("zk mock\t add ephemeral node, node: {}", node);
    }

    @Override
    public void close() {
        logger.info("zk mock\t close zk client");
    }
}
