package qunar.tc.bistoury.proxy.communicate.agent.handler;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.proxy.config.AgentInfoManager;
import qunar.tc.bistoury.proxy.generator.IdGenerator;
import qunar.tc.bistoury.proxy.util.ChannelUtils;
import qunar.tc.bistoury.proxy.util.FutureSuccessCallBack;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.RequestPayloadHolder;
import qunar.tc.bistoury.serverside.metrics.Metrics;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 13:45
 */
@Service
public class AgentInfoRefreshProcessor implements AgentMessageProcessor {

    @Autowired
    private AgentInfoManager agentInfoManager;

    @Autowired
    private IdGenerator generator;

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_REFRESH_AGENT_INFO.getCode());
    }

    @Override
    public void process(final ChannelHandlerContext ctx, Datagram message) {
        Metrics.counter("agent_info_refresh").inc();
        String ip = ChannelUtils.getIp(ctx.channel());
        ListenableFuture<Map<String, String>> agentInfoFuture = agentInfoManager.getAgentInfo(ip);
        Futures.addCallback(agentInfoFuture, (FutureSuccessCallBack<Map<String, String>>) agentInfo ->
                Optional.ofNullable(agentInfo)
                        .map(AgentInfoRefreshProcessor.this::createAgentInfoResponse)
                        .ifPresent((ctx::writeAndFlush)), MoreExecutors.directExecutor());
    }

    private Datagram createAgentInfoResponse(Map<String, String> agentInfo) {
        String data = JacksonSerializer.serialize(agentInfo);
        return RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_REFRESH_AGENT_INFO.getCode(), generator.generateId(), new RequestPayloadHolder(data));
    }
}
