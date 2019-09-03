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

package qunar.tc.bistoury.serverside.store;

import org.apache.curator.utils.ZKPaths;
import qunar.tc.bistoury.serverside.common.registry.RegistryType;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2018 2018/11/9 11:27
 */
public class RegistryStore {

    private static final String DEFAULT_ZK = "register.address";

    private static final String REGISTRY_CONFIG = "registry.properties";

    private String newBaseRoot = "/bistoury/proxy/new/group/";

    private String zkAddress;

    private String pathForNewUi;

    private int registryTypeCode = 0;

    @PostConstruct
    public void init() {
        Map<String, String> registries = DynamicConfigLoader.load(REGISTRY_CONFIG).asMap();
        newBaseRoot = registries.getOrDefault("register.namespace", newBaseRoot);
        zkAddress = registries.get(DEFAULT_ZK);
        pathForNewUi = ZKPaths.makePath(newBaseRoot, "ui");
        registryTypeCode = Integer.parseInt(registries.getOrDefault("register.type", "0"));
    }


    public String getZkAddress() {
        return zkAddress;
    }

    public String getProxyZkPathForNewUi() {
        return pathForNewUi;
    }

    public RegistryType getRegistryType() {
        return RegistryType.fromCode(registryTypeCode);
    }
}
