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

package qunar.tc.bistoury.clientside.common.meta;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/1/10 15:40
 */
public class MetaStores {

    private static final MetaStore sharedMetaStore = new DefaultMetaStore(Maps.newHashMap());

    private static final Map<String, MetaStore> appMetaStores = Maps.newConcurrentMap();

    public static MetaStore getSharedMetaStore() {
        return sharedMetaStore;
    }

    public static MetaStore getMetaStore(String appCode) {
        appMetaStores.put(appCode, new DefaultMetaStore());
        return appMetaStores.get(appCode);
    }

    public static void initMetaStores(String appCode, Map<String, String> pidInfoStructured) {
        DefaultMetaStore defaultMetaStore = new DefaultMetaStore(pidInfoStructured);
        appMetaStores.put(appCode, defaultMetaStore);
    }
}
