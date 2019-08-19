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

package qunar.tc.bistoury.remoting.netty;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.AgentConstants;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.pid.PidUtils;
import qunar.tc.bistoury.agent.common.task.AgentGlobalTaskInitializer;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.command.CommandSerializer;
import qunar.tc.bistoury.remoting.protocol.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2018 2018/10/22 17:30
 */
@ChannelHandler.Sharable
public class RequestHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final TypeReference<Map<String, String>> PID_RELATED_TYPE_REFERENCE =
            new TypeReference<Map<String, String>>() {
            };

    private final Map<Integer, Processor> processorMap;
    private final CodeTypeMappingStore codeTypeMappingStore = CodeTypeMappingStores.getInstance();


    public RequestHandler(List<Processor> processors) {
        ImmutableMap.Builder<Integer, Processor> builder = new ImmutableMap.Builder<>();
        for (Processor<?> processor : processors) {
            for (Integer type : processor.types()) {
                builder.put(type, processor);
            }
        }
        processorMap = builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("request process error", cause);
        String requestId = ctx.channel().attr(AgentConstants.attributeKey).get();
        if (requestId != null) {
            RemotingHeader requestHeader = new RemotingHeader();
            requestHeader.setId(requestId);
            String errorMessage = "request process error, " + cause.getClass().getName() + ": " + cause.getMessage();
            ResponseWriter.getInstance().writeEOF(ctx, errorMessage, requestHeader);
        }
        ctx.channel().close();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        final Datagram datagram = (Datagram) msg;
        RemotingHeader header = datagram.getHeader();
        int code = header.getCode();
        String id = header.getId();
        String appCode = header.getProperties().get(AgentConstants.APP_CODE);

        if (code != ResponseCode.RESP_TYPE_HEARTBEAT.getCode()) {
            logger.info("agent receive request: id={}, sourceIp={}, code={}", id, ctx.channel().remoteAddress(), code);

            updateMetaStore(header);
            if (code == CommandCode.REQ_TYPE_AGENT_SERVER_PID_CONFIG_INFO_FETCH.getCode()) {
                AgentGlobalTaskInitializer.init();
                return;
            }
        }

        final ResponseHandler handler = NettyExecuteHandler.of(header, ctx);
        ctx.channel().attr(AgentConstants.attributeKey).set(id);

        Processor processor = processorMap.get(code);
        if (processor == null) {
            handler.handleError(new IllegalArgumentException("unknown code [" + code + "]"));
            return;
        }

        String command = CommandSerializer.readCommand(datagram.getBody());
        int index = command.indexOf(BistouryConstants.FILL_PID);
        if (index >= 0) {
            int pid = PidUtils.getPid(appCode);
            if (pid < 0) {
                handler.handleError(ErrorCode.PID_ERROR.getCode());
                handler.handleEOF();
                return;
            }
            command = command.replace(BistouryConstants.FILL_PID, String.valueOf(pid));
        }

        Class<?> commandType = codeTypeMappingStore.getMappingType(code);

        processor.process(header, commandType.cast(CommandSerializer.deserializeCommand(command, commandType)), handler);
    }

    private static void updateMetaStore(RemotingHeader header) {
        MetaStore sharedMetaStore = MetaStores.getSharedMetaStore();

        Map<String, String> properties = header.getProperties();
        String supportGetPidFromProxy = properties.get(AgentConstants.SUPPORT_GET_PID_FROM_PROXY);
        if (Strings.isNullOrEmpty(supportGetPidFromProxy) || supportGetPidFromProxy.equalsIgnoreCase(Boolean.FALSE.toString())) {
            sharedMetaStore.put(AgentConstants.SUPPORT_GET_PID_FROM_PROXY, Boolean.FALSE.toString());
        } else {
            sharedMetaStore.put(AgentConstants.SUPPORT_GET_PID_FROM_PROXY, Boolean.TRUE.toString());
            String pidInfoInJson = properties.get(AgentConstants.AGENT_SERVER_PID_INFO);
            Map<String, String> pidInfo = JacksonSerializer.deSerialize(pidInfoInJson, PID_RELATED_TYPE_REFERENCE);
            if (pidInfo != null && pidInfo.size() > 0) {
                for (Map.Entry<String, String> entry : pidInfo.entrySet()) {
                    MetaStore appMetaStore = MetaStores.getAppMetaStore(entry.getKey());
                    appMetaStore.put(AgentConstants.PID, entry.getValue());
                }
                Set<String> appCodes = pidInfo.keySet();
                String appCodesDeployOnAgentServer = AgentConstants.COMMA_JOINER.join(appCodes);
                sharedMetaStore.put(AgentConstants.APP_CODES_DEPLOY_ON_AGENT_SERVER_COMMA_SPLIT, appCodesDeployOnAgentServer);
            }
        }
    }
}
