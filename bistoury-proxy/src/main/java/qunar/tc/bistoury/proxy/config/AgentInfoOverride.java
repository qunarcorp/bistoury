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

package qunar.tc.bistoury.proxy.config;

import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leix.xie
 * @date 2019/5/6 14:54
 * @describe
 */
@Service
public class AgentInfoOverride {
    private static final Logger logger = LoggerFactory.getLogger(AgentInfoOverride.class);
    private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();
    private volatile List<AgentConfigOverride> agentConfigOverrides;

    AgentInfoOverride() {
        DynamicConfigLoader.<LocalDynamicConfig>load("agent_config_override.properties", false)
                .addListener(conf -> parseAgentConfigOverride(conf.asMap()));
    }

    private void parseAgentConfigOverride(Map<String, String> configs) {
        List<AgentConfigOverride> agentConfigOverrideList = new ArrayList<>();
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            try {
                List<String> list = SPLITTER.splitToList(entry.getValue());
                if (list.size() == 2) {
                    String key = entry.getKey();
                    int minVersion = Integer.valueOf(list.get(0));
                    String overrideValue = list.get(1);
                    agentConfigOverrideList.add(new AgentConfigOverride(key, overrideValue, minVersion));
                } else {
                    logger.error("Error configuration format, key: {}, overrideValue: {}", entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                logger.error("parse agent config error, key: {}, overrideValue: {}", entry.getKey(), entry.getValue(), e);
            }
        }
        this.agentConfigOverrides = agentConfigOverrideList;
    }

    /**
     * @author leix.xie
     * @date 2019/5/6 16:03
     * @describe 覆盖版本低于指定版本的配置
     */
    public void overrideAgentInfo(Map<String, String> agentInfo, final int version) {
        for (AgentConfigOverride agentConfig : agentConfigOverrides) {
            if (version < agentConfig.getMinVersion()) {
                agentInfo.put(agentConfig.getKey(), agentConfig.getOverrideValue());
            }
        }
    }

    static class AgentConfigOverride {
        private String key;
        private String overrideValue;
        private int minVersion;

        public AgentConfigOverride(String key, String overrideValue, int minVersion) {
            this.key = key;
            this.overrideValue = overrideValue;
            this.minVersion = minVersion;
        }

        public String getKey() {
            return key;
        }


        public String getOverrideValue() {
            return overrideValue;
        }


        public int getMinVersion() {
            return minVersion;
        }
    }
}