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

package qunar.tc.bistoury.proxy.communicate.agent.handler;

import com.google.common.collect.ImmutableSet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.proxy.communicate.agent.DefaultAgentConnectionStore;
import qunar.tc.bistoury.proxy.generator.IdGenerator;
import qunar.tc.bistoury.proxy.generator.SessionIdGenerator;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.RequestPayloadHolder;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/5/14 17:39
 */
@Service
public class ProxyHeartbeatProcessor implements AgentMessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ProxyHeartbeatProcessor.class);

    @Autowired
    private AgentConnectionStore connectionStore = new DefaultAgentConnectionStore();

    @Autowired
    private IdGenerator idGenerator = new SessionIdGenerator();

    private final Datagram heartbeatResponse = initHeartbeatResponse();

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(ResponseCode.RESP_TYPE_HEARTBEAT.getCode());
    }

    @Override
    public void process(ChannelHandlerContext ctx, Datagram message) {
        logger.debug("receive heartbeat, {}", message);
        String ip = getIp(ctx.channel());
        message.release();
        connectionStore.register(ip, message.getHeader().getAgentVersion(), ctx.channel());
        ctx.channel().writeAndFlush(heartbeatResponse);
    }

    private String getIp(Channel channel) {
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        return address.getAddress().getHostAddress();
    }

    private Datagram initHeartbeatResponse() {
        return RemotingBuilder.buildRequestDatagram(ResponseCode.RESP_TYPE_HEARTBEAT.getCode(), idGenerator.generateId(), new RequestPayloadHolder(""));
    }
}
