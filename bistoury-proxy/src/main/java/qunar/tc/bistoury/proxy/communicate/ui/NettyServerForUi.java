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

package qunar.tc.bistoury.proxy.communicate.ui;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.application.api.AppServerService;
import qunar.tc.bistoury.proxy.communicate.NettyServer;
import qunar.tc.bistoury.proxy.communicate.SessionManager;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.proxy.communicate.ui.command.CommunicateCommandStore;
import qunar.tc.bistoury.proxy.communicate.ui.handler.*;
import qunar.tc.bistoury.proxy.generator.IdGenerator;
import qunar.tc.bistoury.proxy.util.AppCenterServerFinder;
import qunar.tc.bistoury.serverside.agile.Conf;
import qunar.tc.bistoury.serverside.common.encryption.DefaultRequestEncryption;
import qunar.tc.bistoury.serverside.common.encryption.RSAEncryption;

/**
 * @author zhenyu.nie created on 2019 2019/5/16 11:33
 */
public class NettyServerForUi implements NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerForUi.class);


    private static final int DEFAULT_WRITE_LOW_WATER_MARK = 64 * 1024;

    private static final int DEFAULT_WRITE_HIGH_WATER_MARK = 128 * 1024;

    private static final EventLoopGroup BOSS = new NioEventLoopGroup(1, new ThreadFactoryBuilder().setNameFormat("ui-netty-server-boss").build());

    private static final EventLoopGroup WORKER = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setNameFormat("ui-netty-server-worker").build());

    private final int port;

    private final IdGenerator idGenerator;

    private final UiConnectionStore uiConnectionStore;

    private final AgentConnectionStore agentConnectionStore;

    private final SessionManager sessionManager;

    private final CommunicateCommandStore commandStore;

    private final AppServerService appServerService;

    private final AppCenterServerFinder serverFinder;

    private volatile Channel channel;

    public NettyServerForUi(Conf conf,
                            IdGenerator idGenerator,
                            CommunicateCommandStore commandStore,
                            UiConnectionStore uiConnectionStore,
                            AgentConnectionStore agentConnectionStore,
                            SessionManager sessionManager, AppServerService appServerService) {
        this.port = conf.getInt("server.port", -1);
        this.idGenerator = idGenerator;
        this.uiConnectionStore = uiConnectionStore;
        this.agentConnectionStore = agentConnectionStore;
        this.sessionManager = sessionManager;
        this.commandStore = commandStore;
        this.appServerService = appServerService;
        this.serverFinder = new AppCenterServerFinder(this.appServerService);
    }

    @Override
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, DEFAULT_WRITE_LOW_WATER_MARK)
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, DEFAULT_WRITE_HIGH_WATER_MARK)
                .channel(NioServerSocketChannel.class)
                .group(BOSS, WORKER)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pip = ch.pipeline();
                        pip.addLast(new IdleStateHandler(0, 0, 30 * 60 * 1000))
                                .addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(1024 * 1024))
                                .addLast(new WebSocketServerProtocolHandler("/ws"))
                                .addLast(new WebSocketFrameAggregator(1024 * 1024 * 1024))
                                .addLast(new RequestDecoder(new DefaultRequestEncryption(new RSAEncryption())))
                                .addLast(new WebSocketEncoder())
                                .addLast(new TabHandler())
                                .addLast(new HostsValidatorHandler(serverFinder))
                                .addLast(new UiRequestHandler(
                                        commandStore,
                                        uiConnectionStore,
                                        agentConnectionStore,
                                        sessionManager));
                    }
                });
        try {
            this.channel = bootstrap.bind(port).sync().channel();
            logger.info("client server startup successfully, port {}", port);
        } catch (Exception e) {
            logger.error("netty server for ui start fail", e);
            throw Throwables.propagate(e);
        }
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public void stop() {
        try {
            BOSS.shutdownGracefully().sync();
            WORKER.shutdownGracefully().sync();
            channel.close();
        } catch (InterruptedException e) {
            logger.error("ui server close error", e);
        }
    }
}
