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

package qunar.tc.bistoury.remoting.protocol;

import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.ErrorResponsePayloadHolder;

/**
 * @author leix.xie
 * @date 2019/5/13 14:11
 * @describe
 */
public class RemotingBuilder {

    public static Datagram buildRequestDatagram(final int code, final String id, final PayloadHolder payloadHolder) {
        final Datagram datagram = new Datagram();
        datagram.setHeader(buildRemotingHeader(code, id));
        datagram.setPayloadHolder(payloadHolder);
        return datagram;
    }

    public static RemotingHeader buildRemotingHeader(final int code, final String id) {
        RemotingHeader header = new RemotingHeader();
        header.setCode(code);
        header.setId(id);
        header.setVersion(RemotingHeader.PROTOCOL_VERSION);
        header.setAgentVersion(RemotingHeader.AGENT_VERSION);
        header.setFlag(RemotingHeader.DEFAULT_FLAG);
        return header;
    }

    public static Datagram buildResponseDatagram(final int code, final RemotingHeader requestHeader, final PayloadHolder payloadHolder) {
        final Datagram datagram = new Datagram();
        datagram.setHeader(buildRemotingHeader(code, requestHeader));
        datagram.setPayloadHolder(payloadHolder);
        return datagram;
    }

    public static Datagram buildErrorResponseDatagram(final int errorCode, final String message) {
        final Datagram datagram = new Datagram();
        RemotingHeader header = new RemotingHeader();
        header.setCode(ResponseCode.RESP_TYPE_EXCEPTION.getCode());
        header.setFlag(RemotingHeader.DEFAULT_FLAG);
        datagram.setHeader(header);
        datagram.setPayloadHolder(new ErrorResponsePayloadHolder(errorCode, message));
        return datagram;
    }

    public static Datagram buildEmptyResponseDatagram(final int code, final RemotingHeader requestHeader) {
        return buildResponseDatagram(code, requestHeader, null);
    }

    private static RemotingHeader buildRemotingHeader(final int code, final RemotingHeader requestHeader) {
        RemotingHeader header = new RemotingHeader();
        header.setCode(code);
        header.setVersion(RemotingHeader.PROTOCOL_VERSION);
        header.setAgentVersion(RemotingHeader.AGENT_VERSION);
        header.setFlag(RemotingHeader.DEFAULT_FLAG);
        header.setId(requestHeader.getId());
        header.setProperties(requestHeader.getProperties());
        return header;
    }

    public static Datagram buildAgentRequest(final int code, PayloadHolder holder) {
        final Datagram datagram = new Datagram();
        datagram.setHeader(buildRemotingHeader(code, ""));
        datagram.setPayloadHolder(holder);
        return datagram;
    }

    public static Datagram buildFullResponseDatagram(final RemotingHeader responseHeader, final PayloadHolder payloadHolder) {
        final Datagram datagram = new Datagram();
        datagram.setHeader(responseHeader);
        datagram.setPayloadHolder(payloadHolder);
        return datagram;
    }
}
