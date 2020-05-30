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

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.job.DefaultResponseJobStore;
import qunar.tc.bistoury.agent.common.job.ResponseJobStore;
import qunar.tc.bistoury.commands.HeartbeatProcessor;
import qunar.tc.bistoury.commands.MetaRefreshProcessor;
import qunar.tc.bistoury.commands.MetaRefreshTipProcessor;
import qunar.tc.bistoury.remoting.coder.AgentDecoder;
import qunar.tc.bistoury.remoting.coder.AgentEncoder;
import qunar.tc.bistoury.remoting.netty.*;

import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenyu.nie created on 2018 2018/10/25 16:37
 */
class AgentNettyClient {

    private static final Logger logger = LoggerFactory.getLogger(AgentNettyClient.class);

    private final ProxyConfig proxyConfig;

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup workGroup;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final SettableFuture<Void> started = SettableFuture.create();

    private volatile Channel channel;

    public AgentNettyClient(ProxyConfig proxyConfig, EventLoopGroup workGroup) {
        this.proxyConfig = proxyConfig;
        this.workGroup = workGroup;
    }

    public void start() {
        final AgentInfoRefreshTask refreshTask = new AgentInfoRefreshTask();
        final HeartbeatTask heartbeatTask = new HeartbeatTask(proxyConfig.getHeartbeatSec());
        int heartbeatTimeoutSec = proxyConfig.getHeartbeatSec() * 2 + proxyConfig.getHeartbeatSec() / 2;
        final IdleStateHandler idleStateHandler = new IdleStateHandler(heartbeatTimeoutSec, 0, 0, TimeUnit.SECONDS);

        List<TaskFactory> taskFactories = ImmutableList.copyOf(ServiceLoader.load(TaskFactory.class));
        final TaskStore taskStore = new DefaultTaskStore();
        final ResponseJobStore jobStore = new DefaultResponseJobStore();
        TaskProcessor taskProcessor = new TaskProcessor(jobStore, taskStore, taskFactories);
        final RequestHandler requestHandler = new RequestHandler(ImmutableList.<Processor>of(
                new JobPauseProcessor(jobStore),
                new JobResumeProcessor(jobStore),
                new CancelProcessor(taskStore),
                new HeartbeatProcessor(),
                new MetaRefreshProcessor(),
                new MetaRefreshTipProcessor(),
                taskProcessor));

        final ConnectionManagerHandler connectionManagerHandler = new ConnectionManagerHandler(jobStore);

        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast("encoder", new AgentEncoder())
                                .addLast("decoder", new AgentDecoder())
                                .addLast("idle", idleStateHandler)
                                .addLast(requestHandler)
                                .addLast(connectionManagerHandler);
                    }
                });
        bootstrap.connect(proxyConfig.getIp(), proxyConfig.getPort()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("bistoury netty client start success, {}", proxyConfig);
                    channel = future.channel();
                    closeFuture(jobStore, taskStore);
                    running.compareAndSet(false, true);
                    started.set(null);
                    heartbeatTask.start(channel, running);
                    refreshTask.start(channel, running);
                } else {
                    started.set(null);
                    logger.warn("bistoury netty client start fail, {}", proxyConfig, future.cause());
                }
            }
        });


        try {
            started.get();
        } catch (InterruptedException e) {
            logger.error("start bistoury netty client error", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("start bistoury netty client error", e);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    private void closeFuture(final ResponseJobStore jobStore, final TaskStore taskStore) {
        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                jobStore.close();
                taskStore.close();
            }
        });
    }

    @ChannelHandler.Sharable
    private class ConnectionManagerHandler extends ChannelDuplexHandler {

        private final ResponseJobStore jobStore;

        private ConnectionManagerHandler(ResponseJobStore jobStore) {
            this.jobStore = jobStore;
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
            logger.warn("agent netty client channel disconnect, {}", ctx.channel());
            destroyAndSync();
            super.disconnect(ctx, future);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            logger.info("agent netty client channel active, {}", ctx.channel());
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            logger.warn("agent netty client channel inactive, {}", ctx.channel());
            destroyAndSync();
            super.channelInactive(ctx);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                logger.warn("agent netty client idle, {}", ctx.channel());
                destroyAndSync();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.error("agent netty client error, {}", ctx.channel(), cause);
            destroyAndSync();
        }

        @Override
        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
            boolean writable = channel.isWritable();
            logger.info("agent writability changed to {}", writable);
            jobStore.setWritable(writable);
            super.channelWritabilityChanged(ctx);
        }
    }

    public void destroyAndSync() {
        if (running.compareAndSet(true, false)) {
            logger.warn("agent netty client destroy, {}", channel);
            try {
                channel.close();
            } catch (Exception e) {
                logger.error("close channel error", e);
            }
        }
    }
}
