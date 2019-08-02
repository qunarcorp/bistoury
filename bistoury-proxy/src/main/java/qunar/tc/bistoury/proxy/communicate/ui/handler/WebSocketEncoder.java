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

package qunar.tc.bistoury.proxy.communicate.ui.handler;

import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import qunar.tc.bistoury.proxy.util.ChannelUtils;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;

import java.util.List;

/**
 * @author leix.xie
 * @date 2019/5/16 11:35
 * @describe
 */
public class WebSocketEncoder extends MessageToMessageEncoder<Datagram> {
    private static final int BODY_LEN = 4;

    @Override
    protected void encode(ChannelHandlerContext ctx, Datagram msg, List<Object> out) throws Exception {
        ByteBuf result = ctx.alloc().buffer();
        RemotingHeader header = msg.getHeader();
        result.writeLong(-1);
        //code转换，将新agent的code转换为ui能识别的code
        Optional<ResponseCode> optional = ResponseCode.valueOfCode(header.getCode());
        if (optional.isPresent()) {
            result.writeInt(optional.get().getOldCode());
        } else {
            result.writeInt(ResponseCode.RESP_TYPE_ALL_END.getOldCode());
        }
        result.writeInt(ChannelUtils.getIpToN(ctx.channel()));

        ByteBuf body = msg.getBody();
        //proxy返回给ui的body为空
        if (body == null) {
            int start = result.writerIndex();
            result.writerIndex(start + BODY_LEN);
            msg.writeBody(result);

            result.markWriterIndex();
            int bodyLen = result.writerIndex() - start - BODY_LEN;
            result.writerIndex(start);
            result.writeInt(bodyLen);
            result.resetWriterIndex();
        } else {
            int bodyLen = body.readableBytes();
            result.writeInt(bodyLen);

            byte[] bytes = new byte[bodyLen];
            body.readBytes(bytes);
            result.writeBytes(bytes);
        }

        out.add(new BinaryWebSocketFrame(result));
    }
}
