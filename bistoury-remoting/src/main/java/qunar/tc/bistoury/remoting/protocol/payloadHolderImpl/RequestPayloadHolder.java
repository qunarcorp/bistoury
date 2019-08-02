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

package qunar.tc.bistoury.remoting.protocol.payloadHolderImpl;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.protocol.PayloadHolder;

/**
 * @author zhenyu.nie created on 2019 2019/5/23 18:31
 */
public class RequestPayloadHolder implements PayloadHolder {

    private final Object obj;

    public RequestPayloadHolder(Object obj) {
        this.obj = obj;
    }

    @Override
    public void writeBody(ByteBuf out) {
        if (obj instanceof String) {
            out.writeBytes(((String) obj).getBytes(Charsets.UTF_8));
        } else {
            byte[] bytes = JacksonSerializer.serializeToBytes(obj);
            out.writeBytes(bytes);
        }
    }
}
