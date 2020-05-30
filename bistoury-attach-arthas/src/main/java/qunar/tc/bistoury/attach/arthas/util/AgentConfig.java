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

package qunar.tc.bistoury.attach.arthas.util;

import qunar.tc.bistoury.clientside.common.meta.MetaStore;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/3/14 17:02
 */
public class AgentConfig {

    private MetaStore metaStore;

    public AgentConfig(MetaStore metaStore) {
        this.metaStore = metaStore;
    }

    public synchronized boolean update(Map<String, String> input) {
        if (!metaStore.getAgentInfo().equals(input)) {
            metaStore.update(input);
            return true;
        }
        return false;
    }
}
