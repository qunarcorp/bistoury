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
import qunar.tc.bistoury.remoting.protocol.ErrorCode;
import qunar.tc.bistoury.remoting.protocol.PayloadHolder;

/**
 * @author leix.xie
 * @date 2019/5/27 16:27
 * @describe
 */
public class ErrorResponsePayloadHolder implements PayloadHolder {
    private ErrorMsg errorMsg;

    public ErrorResponsePayloadHolder(int code) {
        this(code, null);
    }

    public ErrorResponsePayloadHolder(String message) {
        this(ErrorCode.SYSTEM_EXCEPTION.getCode(), message);
    }

    public ErrorResponsePayloadHolder(int code, String message) {
        this.errorMsg = new ErrorMsg(code, message);
    }

    @Override
    public void writeBody(ByteBuf out) {
        String error = JacksonSerializer.serialize(errorMsg);
        out.writeBytes(error.getBytes(Charsets.UTF_8));
    }

    private static class ErrorMsg {
        private int code;
        private String message;

        public ErrorMsg(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
