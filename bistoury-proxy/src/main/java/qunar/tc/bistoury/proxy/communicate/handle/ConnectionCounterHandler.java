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

package qunar.tc.bistoury.proxy.communicate.handle;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.serverside.metrics.Metrics;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 19:08
 */
@ChannelHandler.Sharable
public class ConnectionCounterHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionCounterHandler.class);

    private final AtomicInteger count = new AtomicInteger();

    private final String name;

    public ConnectionCounterHandler(String name) {
        Metrics.gauge("netty_connection_" + name + "_active", count::doubleValue);
        this.name = name;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("{} {} connected", name, ctx.channel().remoteAddress());
        count.incrementAndGet();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("{} {} disconnected", name, ctx.channel().remoteAddress());
        count.decrementAndGet();
        super.channelInactive(ctx);
    }
}
