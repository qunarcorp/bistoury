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

import com.google.common.base.Charsets;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.ErrorResponsePayloadHolder;
import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.ResponsePayloadHolder;

/**
 * @author sen.chai
 * @date 15-6-15
 */
public class ResponseWriter {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseWriter.class);

    private static ResponseWriter INSTANCE = new ResponseWriter();

    private ResponseWriter() {
    }

    public static ResponseWriter getInstance() {
        return INSTANCE;
    }

    public void writeNormal(ChannelHandlerContext ctx, byte[] contentBytes, RemotingHeader requestHeader) {
        writeResponse(ctx, contentBytes, ResponseCode.RESP_TYPE_CONTENT.getCode(), requestHeader);
    }

    public void writeNormal(ChannelHandlerContext ctx, String content, RemotingHeader requestHeader) {
        writeResponse(ctx, content, ResponseCode.RESP_TYPE_CONTENT.getCode(), requestHeader);
    }

    public void writeError(ChannelHandlerContext ctx, int errorCode, RemotingHeader requestHeader) {
        Datagram datagram = RemotingBuilder.buildResponseDatagram(ResponseCode.RESP_TYPE_EXCEPTION.getCode(), requestHeader, new ErrorResponsePayloadHolder(errorCode));
        ctx.writeAndFlush(datagram);
    }

    public void writeError(ChannelHandlerContext ctx, String content, RemotingHeader requestHeader) {
        Datagram datagram = RemotingBuilder.buildResponseDatagram(ResponseCode.RESP_TYPE_EXCEPTION.getCode(), requestHeader, new ErrorResponsePayloadHolder(content));
        ctx.writeAndFlush(datagram);
    }

    public void writeEOF(ChannelHandlerContext ctx, String content, RemotingHeader requestHeader) {
        writeResponse(ctx, content, ResponseCode.RESP_TYPE_SINGLE_END.getCode(), requestHeader);
    }

    public void writeWithCode(ChannelHandlerContext ctx, String line, int code, RemotingHeader requestHeader) {
        writeResponse(ctx, line.getBytes(Charsets.UTF_8), code, requestHeader);
    }

    public void writeWithCode(ChannelHandlerContext ctx, final byte[] bytes, int code, RemotingHeader requestHeader) {
        writeResponse(ctx, bytes, code, requestHeader);
    }

    private void writeResponse(ChannelHandlerContext ctx, String line, int code, RemotingHeader requestHeader) {
        writeResponse(ctx, line.getBytes(Charsets.UTF_8), code, requestHeader);
    }

    private void writeResponse(ChannelHandlerContext ctx, final byte[] bytes, int code, RemotingHeader requestHeader) {
        Datagram datagram = RemotingBuilder.buildResponseDatagram(code, requestHeader, new ResponsePayloadHolder(bytes));
        ctx.writeAndFlush(datagram);
    }

    public void writeFullResponse(ChannelHandlerContext ctx, final byte[] bytes, RemotingHeader responseHeader) {
        Datagram datagram = RemotingBuilder.buildFullResponseDatagram(responseHeader, new ResponsePayloadHolder(bytes));
        ctx.writeAndFlush(datagram);
    }
}

    