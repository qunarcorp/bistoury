package qunar.tc.bistoury.proxy.communicate.ui.handler;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.proxy.communicate.Session;
import qunar.tc.bistoury.proxy.communicate.SessionManager;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.proxy.communicate.ui.*;
import qunar.tc.bistoury.proxy.communicate.ui.command.CommunicateCommand;
import qunar.tc.bistoury.proxy.communicate.ui.command.CommunicateCommandStore;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.CommunicateCommandProcessor;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.RequestPayloadHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhenyu.nie created on 2019 2019/5/16 16:12
 */
@ChannelHandler.Sharable
public class UiRequestHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(UiRequestHandler.class);

    private static final String CANCEL_SIGN = ".c";

    private UiConnectionStore uiConnectionStore;

    private AgentConnectionStore agentConnectionStore;

    private SessionManager sessionManager;

    private final CommunicateCommandStore commandStore;

    public UiRequestHandler(CommunicateCommandStore commandStore,
                            UiConnectionStore uiConnectionStore,
                            AgentConnectionStore agentConnectionStore,
                            SessionManager sessionManager) {
        this.commandStore = commandStore;
        this.uiConnectionStore = uiConnectionStore;
        this.agentConnectionStore = agentConnectionStore;
        this.sessionManager = sessionManager;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof Datagram)) {
            super.write(ctx, msg, promise);
            return;
        }

        Datagram datagram = (Datagram) msg;

        String id = Strings.nullToEmpty(datagram.getHeader().getId());
        Session session = sessionManager.getSession(id);
        if (session == null) {
            super.write(ctx, msg, promise);
            return;
        }

        Optional<CommunicateCommand> communicateCommand = commandStore.getCommunicateCommand(session.getRequestData().getType());
        if (!communicateCommand.isPresent()) {
            logger.warn("unknown command response, {}", datagram);
            session.finish();
            return;
        }

        Datagram response = communicateCommand.get().getProcessor().prepareResponse(datagram);
        super.write(ctx, response, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof RequestData)) {
            super.channelRead(ctx, msg);
            return;
        }

        @SuppressWarnings("unchecked")
        RequestData<String> inputData = (RequestData<String>) msg;

        UiConnection uiConnection = uiConnectionStore.register(ctx.channel());

        if (inputData.getType() == CommandCode.REQ_TYPE_CANCEL.getCode()) {
            cancelRequest(uiConnection);
            return;
        }

        Optional<CommunicateCommand> command = commandStore.getCommunicateCommand(inputData.getType());
        if (!command.isPresent()) {
            ctx.channel().writeAndFlush(UiResponses.createNoCommandResponse(inputData));
            return;
        }
        CommunicateCommand communicateCommand = command.get();
        if (!communicateCommand.isSupportMulti() && inputData.getAgentServerInfos().size() > 1) {
            ctx.channel().writeAndFlush(UiResponses.createNotSupportMultiResponse(inputData));
            return;
        }

        CommunicateCommandProcessor<?> processor = communicateCommand.getProcessor();
        Optional<? extends RequestData<?>> requestDataOptional = processor.preprocessor(inputData, ctx);
        if (!requestDataOptional.isPresent()) {
            ctx.channel().writeAndFlush(UiResponses.createProcessRequestErrorResponse(inputData));
            return;
        }

        RequestData<?> requestData = requestDataOptional.get();
        List<AgentConnection> agentConnections = Lists.newArrayListWithCapacity(requestData.getAgentServerInfos().size());
        List<String> lessVersionAgents = Lists.newArrayList();
        List<String> noConnectionAgents = Lists.newArrayList();
        for (AgentServerInfo agentServerInfo : requestData.getAgentServerInfos()) {
            Optional<AgentConnection> agentConnection = agentConnectionStore.getConnection(agentServerInfo.getAgentId());
            if (agentConnection.isPresent()) {
                if (agentConnection.get().getVersion() > communicateCommand.getMinAgentVersion()) {
                    agentConnections.add(agentConnection.get());
                } else {
                    lessVersionAgents.add(agentServerInfo.getAgentId());
                }
            } else {
                noConnectionAgents.add(agentServerInfo.getAgentId());
            }
        }

        noConnectionAgents.stream()
                .map(noConnectionAgent -> UiResponses.createNoConnectionResponse(noConnectionAgent, requestData))
                .forEach(uiConnection::write);
        lessVersionAgents.stream().
                map(lessVersionAgent -> UiResponses.createLessVersionResponse(lessVersionAgent, requestData))
                .forEach(uiConnection::write);

        if (agentConnections.isEmpty()) {
            uiConnection.write(UiResponses.createFinishResponse(requestData));
            return;
        }

        List<Session> sessions = agentConnections.stream()
                .map((agentConnection -> sendMessage(requestData, processor, agentConnection, uiConnection)))
                .collect(Collectors.toList());

        ListenableFuture<List<Session.State>> sessionsFuture = Futures.successfulAsList(sessions.stream().map(Session::getEndState).collect(Collectors.toList()));
        sessionsFuture.addListener(() -> uiConnection.write(UiResponses.createFinishResponse(requestData)), MoreExecutors.directExecutor());
    }

    private Session sendMessage(RequestData requestData, CommunicateCommandProcessor<?> processor, AgentConnection agentConnection, UiConnection uiConnection) {
        Session session = sessionManager.create(requestData, agentConnection, uiConnection);
        @SuppressWarnings("unchecked")
        Datagram datagram = processor.prepareRequest(session.getId(), requestData, agentConnection.getAgentId());
        session.writeToAgent(datagram);
        return session;
    }

    private void cancelRequest(UiConnection uiConnection) {
        Set<Session> sessions = sessionManager.getSessionByUiConnection(uiConnection);
        for (Session session : sessions) {
            String id = session.getId();
            Datagram datagram = RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_CANCEL.getCode(), id + CANCEL_SIGN, new RequestPayloadHolder(id));
            session.writeToAgent(datagram);
            session.finish();
        }
    }

}
