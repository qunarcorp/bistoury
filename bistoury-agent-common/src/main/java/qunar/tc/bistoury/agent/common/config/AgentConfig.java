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

package qunar.tc.bistoury.agent.common.config;

import qunar.tc.bistoury.clientside.common.meta.MetaStore;

/**
 * @author zhenyu.nie created on 2019 2019/3/25 14:37
 */
public class AgentConfig {


    private final MetaStore metaStore;

    public AgentConfig(MetaStore metaStore) {
        this.metaStore = metaStore;
    }

    public boolean isHeapHistoOn() {
        return metaStore.getBooleanProperty("heapJMapHistoOn", false);
    }

    public int getHeapHistoStoreSize() {
        return metaStore.getIntProperty("heapHisto.store.size", 100);
    }

    public int getAgentInfoRefreshInterval() {
        return metaStore.getIntProperty("agent.refresh.interval.min", 10);
    }

    public boolean isCpuJStackOn() {
        return metaStore.getBooleanProperty("cpuJStackOn", false);
    }

}
