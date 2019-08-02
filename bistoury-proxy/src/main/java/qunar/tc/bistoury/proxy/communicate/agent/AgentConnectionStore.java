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

package qunar.tc.bistoury.proxy.communicate.agent;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.Optional;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 19:42
 */
public interface AgentConnectionStore {

    AgentConnection register(String agentId, int agentVersion, Channel channel);

    Optional<AgentConnection> getConnection(String agentId);

    Map<String, AgentConnection> getAgentConnection();

    Map<String,AgentConnection> searchConnection(String agentId);
}
