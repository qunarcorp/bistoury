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
import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import qunar.tc.bistoury.remoting.protocol.PayloadHolder;

/**
 * @author leix.xie
 * @date 2019/6/10 16:50
 * @describe
 */
public class ResponseStringPayloadHolder implements PayloadHolder {
    private String data;

    public ResponseStringPayloadHolder(String data) {
        this.data = data;
    }

    @Override
    public void writeBody(ByteBuf out) {
        out.writeBytes(Strings.nullToEmpty(data).getBytes(Charsets.UTF_8));
    }
}
