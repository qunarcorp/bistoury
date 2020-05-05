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

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.NamedThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhenyu.nie created on 2018 2018/10/25 16:00
 */
public class AgentClient {

    private static final Logger logger = LoggerFactory.getLogger(AgentClient.class);

    private static final ScheduledExecutorService FAILOVER_EXECUTOR = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("bistoury-agent-failover", true));

    private static final AgentClient INSTANCE = new AgentClient();

    public static AgentClient getInstance() {
        return INSTANCE;
    }

    private final EventLoopGroup WORK_GROUP = new NioEventLoopGroup(Integer.parseInt(System.getProperty("bistoury.agent.workgroup.num", "2")), new NamedThreadFactory("bistoury-agent-netty"));

    private boolean start = false;

    private volatile AgentNettyClient nettyClient;

    private AgentClient() {
    }

    public synchronized void start() {
        if (start) {
            return;
        }
        DumpFileCleaner.getInstance().start();
        refreshClient();
        startFailoverTask();
        start = true;
    }

    public void stop() {
        try {
            FAILOVER_EXECUTOR.shutdown();
            if (nettyClient != null) {
                nettyClient.destroyAndSync();
            }
            WORK_GROUP.shutdownGracefully().sync();
        } catch (Exception e) {
            logger.error("bistoury agent shutdown error", e);
        }
    }

    private void refreshClient() {
        logger.info("start refresh bistoury netty client");
        try {
            ProxyConfig proxyConfig = Configs.getProxyConfig();
            logger.info("finish get bistoury proxy config, {}", proxyConfig);
            if (proxyConfig != null) {
                nettyClient = initNettyClient(proxyConfig);
                if (nettyClient.isRunning()) {
                    AgentGlobalTaskInitializer.init();
                }
            }
        } catch (Throwable e) {
            logger.info("refresh bistoury netty client fail", e);
        }
    }

    private AgentNettyClient initNettyClient(ProxyConfig proxyConfig) {
        AgentNettyClient agentNettyClient = new AgentNettyClient(proxyConfig, WORK_GROUP);
        agentNettyClient.start();
        return agentNettyClient;
    }

    private void startFailoverTask() {
        FAILOVER_EXECUTOR.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                // 这里是每分钟判断一次，防止连接proxy的突发流量过大，
                // 但是如果发布或是某台宕机的话，那一个agent可能就要最大1分钟才能重新可用了
                // todo: 要不要立马重连？
                if (nettyClient != null && nettyClient.isRunning()) {
                    return;
                }

                refreshClient();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }
}
