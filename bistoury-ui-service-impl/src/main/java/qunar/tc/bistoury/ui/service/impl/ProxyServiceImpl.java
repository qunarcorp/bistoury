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
