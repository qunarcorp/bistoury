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

package qunar.tc.bistoury.remoting.coder;

import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;
import qunar.tc.bistoury.remoting.util.PayloadHolderUtils;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2018 2018/10/26 11:18
 */
@ChannelHandler.Sharable
public class AgentEncoder extends MessageToByteEncoder<Datagram> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Datagram msg, ByteBuf out) throws Exception {
        int start = out.writerIndex();
        int headerStart = start + RemotingHeader.LENGTH_FIELD;
        out.ensureWritable(RemotingHeader.LENGTH_FIELD);
        out.writerIndex(headerStart);

        RemotingHeader header = msg.getHeader();
        encodeHeader(header, out);

        int headerSize = out.writerIndex() - headerStart;

        msg.writeBody(out);

        int end = out.writerIndex();
        int total = end - start - RemotingHeader.TOTAL_SIZE_LEN;

        out.writerIndex(start);
        out.writeInt(total);
        out.writeShort(headerSize);
        out.writerIndex(end);
    }

    private void encodeHeader(final RemotingHeader header, ByteBuf out) {
        //magic code 4 bytes
        out.writeInt(header.getMagicCode());
        //version 2 bytes
        out.writeShort(header.getVersion());
        //agent version 2byte
        out.writeShort(header.getAgentVersion());
        //id
        PayloadHolderUtils.writeString(Strings.nullToEmpty(header.getId()), out);
        //code 4 bytes
        out.writeInt(header.getCode());
        //flag 4 bytes
        out.writeInt(header.getFlag());
        //properties
        Map<String, String> properties = header.getProperties();
        if (properties != null && !properties.isEmpty()) {
            String data = JacksonSerializer.serialize(properties);
            PayloadHolderUtils.writeString(data, out);
        } else {
            out.writeShort(0);
        }
    }
}