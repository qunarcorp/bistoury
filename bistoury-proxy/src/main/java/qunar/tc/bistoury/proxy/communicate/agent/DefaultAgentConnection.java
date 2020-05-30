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
import qunar.tc.bistoury.proxy.communicate.AbstractConnection;

import java.util.Objects;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 19:45
 */
public class DefaultAgentConnection extends AbstractConnection implements AgentConnection {

    private final String agentId;

    private final int version;

    private final Channel channel;

    public DefaultAgentConnection(String agentId, int version, Channel channel) {
        super("agent", channel);
        this.agentId = agentId;
        this.version = version;
        this.channel = channel;
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultAgentConnection that = (DefaultAgentConnection) o;
        return version == that.version &&
                Objects.equals(agentId, that.agentId) &&
                Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentId, version, channel);
    }
}
