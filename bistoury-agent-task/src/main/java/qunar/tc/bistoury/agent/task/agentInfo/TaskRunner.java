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

package qunar.tc.bistoury.agent.task.agentInfo;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.commands.arthas.telnet.DebugTelnetStore;
import qunar.tc.bistoury.commands.arthas.telnet.Telnet;
import qunar.tc.bistoury.commands.arthas.telnet.TelnetStore;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.common.URLCoder;
import qunar.tc.bistoury.common.VersionUtil;
import qunar.tc.bistoury.remoting.netty.AgentInfoPushReceiver;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static qunar.tc.bistoury.common.BistouryConstants.REQ_AGENT_INFO;

/**
 * @author: leix.xie
 * @date: 2019/2/25 17:51
 * @describe：
 */
public class TaskRunner implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    private final MetaStore metaStore;

    private static final TelnetStore TELNET_STORE = DebugTelnetStore.getInstance();

    private static final String MIN_VERSION = "1.2.8";

    private static final String command = REQ_AGENT_INFO;

    private static final String AGENT_PUSH_INTERVAL_MIN = "agent.push.interval.min";
    private static final int DEFAULT_AGENT_INFO_PUSH_INTERVAL_MINUTES = 1;

    private ListeningScheduledExecutorService executor;

    TaskRunner(String appCode, ListeningScheduledExecutorService executor) {
        this.executor = executor;
        this.metaStore = MetaStores.getMetaStore(appCode);
    }

    @Override
    public void run() {
        Telnet telnet = tryGetTelnet();
        if (telnet != null) {
            try {
                Map<String, String> info = metaStore.getAgentInfo();
                push(info, telnet);
            } finally {
                telnet.close();
            }
        }
        executor.schedule(this, getAgentInfoPushIntervalMinutes(), TimeUnit.MINUTES);
    }

    private int getAgentInfoPushIntervalMinutes() {
        return metaStore.getIntProperty(AGENT_PUSH_INTERVAL_MIN, DEFAULT_AGENT_INFO_PUSH_INTERVAL_MINUTES);
    }

    private void push(Map<String, String> agentInfo, Telnet telnet) {
        try {
            if (!legalVersion(telnet.getVersion())) {
                return;
            }

            if (agentInfo != null) {
                logger.debug("push agent info: {}", agentInfo);
                String newCommand = TaskRunner.command + " " + URLCoder.encode(JacksonSerializer.serialize(agentInfo));
                telnet.write(newCommand);
                //如果不read，一定概率会出现push失败
                telnet.read(newCommand, new AgentInfoPushReceiver());
            }

        } catch (Exception e) {
            logger.error("push agent info error", e);
        }
    }

    private Telnet tryGetTelnet() {
        try {
            return TELNET_STORE.tryGetTelnet();
        } catch (Exception e) {
            logger.error("try get telnet fail", e);
            return null;
        }
    }

    private boolean legalVersion(final String version) {
        try {
            return VersionUtil.greaterEqualThanVersion(version, MIN_VERSION);
        } catch (Exception e) {
            return false;
        }
    }
}
