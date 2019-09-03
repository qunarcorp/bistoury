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

package qunar.tc.bistoury.proxy.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qunar.tc.bistoury.application.api.AppServerService;
import qunar.tc.bistoury.proxy.communicate.Connection;
import qunar.tc.bistoury.proxy.communicate.SessionManager;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.proxy.communicate.agent.NettyServerForAgent;
import qunar.tc.bistoury.proxy.communicate.agent.handler.AgentMessageHandler;
import qunar.tc.bistoury.proxy.communicate.agent.handler.AgentMessageProcessor;
import qunar.tc.bistoury.proxy.communicate.ui.NettyServerForUi;
import qunar.tc.bistoury.proxy.communicate.ui.UiConnectionStore;
import qunar.tc.bistoury.proxy.communicate.ui.command.CommunicateCommandStore;
import qunar.tc.bistoury.serverside.agile.Conf;
import qunar.tc.bistoury.serverside.common.registry.RegistryService;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author leix.xie
 * @date 2019-07-18 11:32
 * @describe
 */
@Component
public class NettyServerManager {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerManager.class);

    @Autowired
    private CommunicateCommandStore commandStore;

    @Autowired
    private UiConnectionStore uiConnectionStore;

    @Autowired
    private AgentConnectionStore agentConnectionStore;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private AppServerService appServerService;

    @Autowired
    private List<AgentMessageProcessor> agentMessageProcessors;

    @Autowired
    private RegistryService registryService;

    private Conf conf;

    private NettyServerForAgent nettyServerForAgent;

    private NettyServerForUi nettyServerForUi;

    @PostConstruct
    public void start() {
        conf = Conf.fromMap(DynamicConfigLoader.load("global.properties").asMap());
        nettyServerForAgent = startAgentServer(conf);
        nettyServerForUi = startUiServer(conf);

        registryService.online();
    }

    @PreDestroy
    public void stop() {
        closeAgentConnections();
        nettyServerForUi.stop();
        nettyServerForAgent.stop();
        registryService.offline();
    }

    private NettyServerForUi startUiServer(Conf conf) {
        NettyServerForUi serverForUi = new NettyServerForUi(conf, commandStore, uiConnectionStore, agentConnectionStore, sessionManager, appServerService);
        serverForUi.start();
        return serverForUi;
    }

    private NettyServerForAgent startAgentServer(Conf conf) {
        AgentMessageHandler handler = new AgentMessageHandler(agentMessageProcessors);
        NettyServerForAgent serverForAgent = new NettyServerForAgent(conf, handler);
        serverForAgent.start();
        return serverForAgent;
    }

    private void closeAgentConnections() {
        Map<String, AgentConnection> agentConnection = agentConnectionStore.getAgentConnection();
        Collection<AgentConnection> connections = agentConnection.values();
        for (Connection connection : connections) {
            connection.close();
        }
    }
}
