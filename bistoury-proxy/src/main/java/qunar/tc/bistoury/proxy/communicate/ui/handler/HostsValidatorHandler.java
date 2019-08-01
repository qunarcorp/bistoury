package qunar.tc.bistoury.proxy.communicate.ui.handler;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.util.CollectionUtils;
import qunar.tc.bistoury.proxy.communicate.ui.AgentServerInfo;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.UiResponses;
import qunar.tc.bistoury.proxy.util.ServerFinder;
import qunar.tc.bistoury.serverside.support.AppServer;

import java.util.Iterator;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019/5/23 11:20
 * @describe
 */
@ChannelHandler.Sharable
public class HostsValidatorHandler extends ChannelInboundHandlerAdapter {
    private final ServerFinder serverFinder;

    public HostsValidatorHandler(ServerFinder serverFinder) {
        this.serverFinder = serverFinder;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestData requestData = (RequestData) msg;
        List<String> hosts = requestData.getHosts();
        if (CollectionUtils.isEmpty(hosts)) {
            ctx.writeAndFlush(UiResponses.createNoHostResponse(requestData));
            return;
        }
        hostValidator(requestData, ctx);
    }

    // 验证 app 与 host 合法性
    private void hostValidator(final RequestData requestData, ChannelHandlerContext ctx) {
        List<AppServer> servers = serverFinder.findAgents(requestData.getApp());
        List<String> userHosts = requestData.getHosts();
        List<AppServer> ret = servers;
        if (userHosts != null && !userHosts.isEmpty()) {
            ret = Lists.newArrayList();
            for (AppServer server : servers) {
                for (String host : userHosts) {
                    if (server.getHost().equals(host)) {
                        ret.add(server);
                    }
                }
            }
        }

        // 兼容旧 common-core, 将没有 logdir 的 server 删除
        Iterator<AppServer> iterator = ret.iterator();
        while (iterator.hasNext()) {
            AppServer next = iterator.next();
            if (Strings.isNullOrEmpty(next.getLogDir())) {
                iterator.remove();
                ctx.writeAndFlush(UiResponses.createNoLogDirResponse(requestData, next.getIp()));
            }
        }

        if (ret.isEmpty()) {
            ctx.writeAndFlush(UiResponses.createHostValidateErrorResponse(requestData));
        } else {
            List<AgentServerInfo> serverInfos = Lists.transform(ret, (server) -> {
                AgentServerInfo agentServerInfo = new AgentServerInfo();
                agentServerInfo.setAgentId(server.getIp());
                agentServerInfo.setIp(server.getIp());
                agentServerInfo.setAppcode(server.getAppCode());
                agentServerInfo.setHost(server.getHost());
                agentServerInfo.setLogdir(server.getLogDir());
                agentServerInfo.setPort(server.getPort());
                return agentServerInfo;
            });
            requestData.setAgentServerInfos(serverInfos);
            ctx.fireChannelRead(requestData);
        }
    }
}
