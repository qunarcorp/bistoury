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

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.apache.curator.utils.ZKPaths;
import qunar.tc.bistoury.serverside.common.registry.RegistryType;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2018 2018/11/9 11:27
 */
public class RegistryStore {

    private static final String REGISTRY_CONFIG = "registry.properties";

    private static final String defaultNamespace = "/bistoury/proxy/new/group/";

    private static final String LOCAL_ZK_TAG_FILE = "/tmp/bistoury/proxy.conf";

    private String zkAddress;

    private String pathForNewUi;

    private int registryTypeCode = 0;

    private Iterable<String> etcdServerUrls;

    private String namespace;

    private static final Splitter LIST_SPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();

    @PostConstruct
    public void init() {
        Map<String, String> registries = DynamicConfigLoader.load(REGISTRY_CONFIG).asMap();
        zkAddress = registries.get("register.zk.address");
        pathForNewUi = ZKPaths.makePath(defaultNamespace, "ui");
        registryTypeCode = Integer.parseInt(registries.getOrDefault("register.type", "-1"));
        etcdServerUrls = LIST_SPLITTER.split(registries.getOrDefault(("register.etcd.uri"), "http://localhost:2379"));
        namespace = registries.getOrDefault("register.namespace", defaultNamespace);
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

    public List<String> getEtcdUris() {
        return ImmutableList.copyOf(etcdServerUrls);
    }

    public String getNamespace() {
        return namespace;
    }

    public  String getLocalZkTagFile() {
        return LOCAL_ZK_TAG_FILE;
    }
}
