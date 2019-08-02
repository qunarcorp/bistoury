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

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

/**
 * @author leix.xie
 * @date 2019/5/13 11:39
 * @describe
 */
public class Datagram {
    private RemotingHeader header;
    private ByteBuf body;
    private PayloadHolder holder;

    public ByteBuf getBody() {
        return body;
    }

    public void setBody(ByteBuf body) {
        this.body = body;
    }

    public void setPayloadHolder(PayloadHolder holder) {
        this.holder = holder;
    }

    public PayloadHolder getHolder() {
        return holder;
    }

    public RemotingHeader getHeader() {
        return header;
    }

    public void setHeader(RemotingHeader header) {
        this.header = header;
    }

    public void writeBody(ByteBuf out) {
        if (holder == null) return;
        holder.writeBody(out);
    }

    public void release() {
        ReferenceCountUtil.safeRelease(body);
    }

    @Override
    public String toString() {
        return "Datagram{" +
                "header=" + header +
                '}';
    }
}
