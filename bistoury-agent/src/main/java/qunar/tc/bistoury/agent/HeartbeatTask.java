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

package qunar.tc.bistoury.agent;

import com.google.common.util.concurrent.MoreExecutors;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.NamedThreadFactory;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.ResponseStringPayloadHolder;
import qunar.tc.bistoury.remoting.util.LocalHost;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenyu.nie created on 2018 2018/10/25 19:40
 */
class HeartbeatTask {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatTask.class);

    private static final ScheduledExecutorService executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("bistoury-agent-heartbeat", true)));

    private final long heartbeatSec;

    private final Datagram heartbeatRequest;

    public HeartbeatTask(long heartbeatSec) {
        this.heartbeatSec = heartbeatSec;
        heartbeatRequest = RemotingBuilder.buildAgentRequest(ResponseCode.RESP_TYPE_HEARTBEAT.getCode(), new ResponseStringPayloadHolder(LocalHost.getLocalHost()));
    }

    public void start(final Channel channel, final AtomicBoolean running) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (running.get()) {
                    channel.writeAndFlush(heartbeatRequest).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                logger.error("send heartbeat error, {}", channel);
                            } else {
                                logger.debug("send heartbeat, {}", channel);
                            }
                        }
                    });

                    executor.schedule(this, heartbeatSec, TimeUnit.SECONDS);
                }
            }
        });
    }
}
