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
