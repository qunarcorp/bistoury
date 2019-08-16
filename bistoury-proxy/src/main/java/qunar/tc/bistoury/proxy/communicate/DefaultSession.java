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

package qunar.tc.bistoury.proxy.communicate;

import com.google.common.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.agent.AgentRelatedDatagramWrapperService;
import qunar.tc.bistoury.proxy.communicate.agent.AgentRelatedDatagramWrapperService.AgentInfo;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.UiConnection;
import qunar.tc.bistoury.proxy.communicate.ui.UiResponses;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 14:55
 */
public class DefaultSession implements Session {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSession.class);

    private final String id;

    private final RequestData requestData;

    private final AgentConnection agentConnection;

    private final UiConnection uiConnection;

    private final SettableFuture<State> resultFuture = SettableFuture.create();

    public DefaultSession(String id, RequestData requestData, AgentConnection agentConnection, UiConnection uiConnection) {
        this.id = id;
        this.requestData = requestData;
        this.agentConnection = agentConnection;
        this.uiConnection = uiConnection;
    }

    @Override
    public void writeToUi(Datagram message) {
        ListenableFuture<WriteResult> result = uiConnection.write(message);
        if (isEndMessage(message)) {
            Futures.addCallback(result, new FutureCallback<WriteResult>() {
                @Override
                public void onSuccess(WriteResult result) {
                    finish();
                }

                @Override
                public void onFailure(Throwable t) {
                    broken();
                }
            }, MoreExecutors.directExecutor());
        }
    }

    @Override
    public void writeToAgent(AgentRelatedDatagramWrapperService agentRelatedDatagramWrapperService, String appCode, Datagram message) {
        String agentServerIp = agentConnection.getAgentServerIp();
        Datagram warpedDatagram = agentRelatedDatagramWrapperService.wrap(appCode, message, new AgentInfo(agentServerIp));
        ListenableFuture<WriteResult> result = agentConnection.write(warpedDatagram);
        Futures.addCallback(result, new FutureCallback<WriteResult>() {

            @Override
            public void onSuccess(WriteResult result) {
                switch (result) {
                    case success:
                        break;
                    case fail:
                        fail();
                        break;
                    default:
                        throw new IllegalStateException("unknown result " + result);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("agent connection write error, {}, {}", agentConnection, message, t);
                fail();
            }

            private void fail() {
                uiConnection.write(UiResponses.createAgentCannotConnect(requestData));
                broken();
            }

        }, MoreExecutors.directExecutor());
    }

    private boolean isEndMessage(Datagram message) {
        int code = message.getHeader().getCode();
        if (code == ResponseCode.RESP_TYPE_SINGLE_END.getCode() || code == ResponseCode.RESP_TYPE_ALL_END.getCode()) {
            return true;
        }
        return false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public RequestData getRequestData() {
        return requestData;
    }

    @Override
    public AgentConnection getAgentConnection() {
        return agentConnection;
    }

    @Override
    public UiConnection getUiConnection() {
        return uiConnection;
    }

    @Override
    public boolean finish() {
        return resultFuture.set(State.finish);
    }

    @Override
    public boolean broken() {
        return resultFuture.set(State.broken);
    }

    @Override
    public ListenableFuture<State> getEndState() {
        return resultFuture;
    }
}
