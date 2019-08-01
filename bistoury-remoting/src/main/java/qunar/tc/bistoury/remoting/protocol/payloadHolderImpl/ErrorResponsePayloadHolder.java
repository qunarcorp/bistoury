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
