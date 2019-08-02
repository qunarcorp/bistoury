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

import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.remoting.protocol.Datagram;

import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/5/14 17:13
 */
@ChannelHandler.Sharable
public class AgentMessageHandler extends SimpleChannelInboundHandler<Datagram> {

    private static final Logger logger = LoggerFactory.getLogger(AgentMessageHandler.class);

    private final Map<Integer, AgentMessageProcessor> processorMap;

    public AgentMessageHandler(List<AgentMessageProcessor> processors) {
        ImmutableMap.Builder<Integer, AgentMessageProcessor> builder = new ImmutableMap.Builder<>();
        for (AgentMessageProcessor processor : processors) {
            for (int code: processor.codes()) {
                builder.put(code, processor);
            }
        }
        processorMap = builder.build();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Datagram message) throws Exception {
        int code = message.getHeader().getCode();
        AgentMessageProcessor messageProcessor = processorMap.get(code);
        if (messageProcessor == null) {
            message.release();
            logger.warn("can not process message code [{}], {}", code, ctx.channel());
            return;
        }

        messageProcessor.process(ctx, message);
    }
}
