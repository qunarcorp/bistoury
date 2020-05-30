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
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.remoting.protocol.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.UiConnection;
import qunar.tc.bistoury.proxy.communicate.ui.UiResponses;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 14:55
 */
public class DefaultSession implements Session {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSession.class);

    private final String id;

    private final boolean supportPause;

    private final RequestData requestData;

    private final AgentConnection agentConnection;

    private final UiConnection uiConnection;

    private final SettableFuture<State> resultFuture = SettableFuture.create();

    private final AtomicBoolean end = new AtomicBoolean(false);

    public DefaultSession(String id, boolean supportPause, RequestData requestData, AgentConnection agentConnection, UiConnection uiConnection) {
        this.id = id;
        this.supportPause = supportPause;
        this.requestData = requestData;
        this.agentConnection = agentConnection;
        this.uiConnection = uiConnection;
    }

    @Override
    public void writeToUi(Datagram message) {
        if (end.get()) {
            return;
        }

        boolean isEndMessage = isEndMessage(message);
        if (isEndMessage) {
            end.set(true);
        } else {
            if (agentConnection.getVersion() < BistouryConstants.MIN_AGENT_VERSION_SUPPORT_JOB_PAUSE &&
                    !agentConnection.isWritable()) {
                broken();
            }
        }

        ListenableFuture<WriteResult> result = uiConnection.write(message);
        if (isEndMessage) {
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
    public void writeToAgent(Datagram message) {
        ListenableFuture<WriteResult> result = agentConnection.write(message);
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
    public boolean isSupportPause() {
        return supportPause;
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
