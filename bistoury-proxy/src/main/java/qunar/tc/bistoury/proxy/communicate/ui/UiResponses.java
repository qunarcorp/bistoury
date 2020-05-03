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

import qunar.tc.bistoury.remoting.protocol.*;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.ResponseStringPayloadHolder;

/**
 * @author zhenyu.nie created on 2019 2019/5/22 11:44
 */
public class UiResponses {

    public static Datagram createNoLogDirResponse(RequestData requestData, final String ip) {
        return RemotingBuilder.buildErrorResponseDatagram(ErrorCode.NO_LOG_DIR.getCode(), null);
    }

    public static Datagram createHostValidateErrorResponse(RequestData requestData) {
        return RemotingBuilder.buildErrorResponseDatagram(ErrorCode.HOST_VALIDATE_ERROR.getCode(), null);
    }

    public static Datagram createNoHostResponse(RequestData requestData) {
        return RemotingBuilder.buildErrorResponseDatagram(ErrorCode.NO_HOST.getCode(), null);
    }

    public static Datagram createAgentCannotConnect(RequestData requestData) {
        return RemotingBuilder.buildErrorResponseDatagram(ErrorCode.AGENT_CANNOT_CONNECT.getCode(), null);
    }

    public static Datagram createNoConnectionResponse(String noConnectionAgent, RequestData requestData) {
        return RemotingBuilder.buildErrorResponseDatagram(ErrorCode.AGENT_NOT_START.getCode(), null);
    }

    public static Datagram createNoCommandResponse(RequestData requestData) {
        return RemotingBuilder.buildErrorResponseDatagram(ErrorCode.UNKNOWN_CODE.getCode(), null);
    }

    public static Datagram createNotSupportMultiResponse(RequestData requestData) {
        return RemotingBuilder.buildErrorResponseDatagram(ErrorCode.NOT_SUPPORT_MULTI.getCode(), null);
    }

    public static Datagram createLessVersionResponse(String lessVersionAgent, RequestData requestData) {
        return RemotingBuilder.buildErrorResponseDatagram(ErrorCode.LESS_VERSION.getCode(), null);
    }

    public static Datagram createFinishResponse(RequestData requestData) {
        return createFinishResponse(requestData, null);
    }

    public static Datagram createFinishResponse(RequestData requestData, final String data) {
        RemotingHeader header = new RemotingHeader();
        return RemotingBuilder.buildResponseDatagram(ResponseCode.RESP_TYPE_ALL_END.getCode(), header, new ResponseStringPayloadHolder(data));
    }

    public static Datagram createProcessRequestErrorResponse(RequestData requestData) {
        return createProcessRequestErrorResponse(requestData, null);
    }

    public static Datagram createProcessRequestErrorResponse(RequestData requestData, String data) {
        return RemotingBuilder.buildErrorResponseDatagram(ErrorCode.COMMAND_PROCESSOR_ERROR.getCode(), data);
    }

    public static Datagram createWrongFrameResponse() {
        return RemotingBuilder.buildErrorResponseDatagram(ErrorCode.COMMAND_PROCESSOR_ERROR.getCode(), null);
    }
}
