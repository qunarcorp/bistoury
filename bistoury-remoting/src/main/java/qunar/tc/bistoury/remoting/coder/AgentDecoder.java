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

import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;
import qunar.tc.bistoury.remoting.util.PayloadHolderUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author sen.chai
 * @date 15-6-30
 */
public class AgentDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(AgentDecoder.class);

    private static final TypeReference PROPERTIES_TYPE = new TypeReference<Map<String, String>>() {
    };

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        if (msg.readableBytes() < RemotingHeader.MIN_TOTAL_SIZE) {
            return;
        }

        int magicCode = msg.getInt(msg.readerIndex() + RemotingHeader.LENGTH_FIELD);
        if (magicCode != RemotingHeader.DEFAULT_MAGIC_CODE) {
            throw new IOException("非法数据，MagicCode=" + Integer.toHexString(magicCode));
        }

        msg.markReaderIndex();
        int total = msg.readInt();
        if (msg.readableBytes() < total) {
            msg.resetReaderIndex();
            return;
        }

        short headerSize = msg.readShort();
        RemotingHeader header = decodeHeader(msg);

        int bodyLength = total - headerSize - RemotingHeader.HEADER_SIZE_LEN;
        ByteBuf bodyData = Unpooled.buffer(bodyLength, bodyLength);
        msg.readBytes(bodyData, bodyLength);

        Datagram datagram = new Datagram();
        datagram.setHeader(header);
        datagram.setBody(bodyData);

        out.add(datagram);

    }

    public RemotingHeader decodeHeader(ByteBuf msg) {
        RemotingHeader header = new RemotingHeader();
        header.setMagicCode(msg.readInt());
        header.setVersion(msg.readShort());
        header.setAgentVersion(msg.readShort());
        header.setId(PayloadHolderUtils.readString(msg));
        header.setCode(msg.readInt());
        header.setFlag(msg.readInt());
        int propertiesLen = msg.readShort();
        if (propertiesLen > 0) {
            byte[] bs = new byte[propertiesLen];
            msg.readBytes(bs);
            header.setProperties(JacksonSerializer.<Map<String, String>>deSerialize(bs, PROPERTIES_TYPE));
        } else {
            header.setProperties(Collections.<String, String>emptyMap());
        }

        return header;
    }
}

    