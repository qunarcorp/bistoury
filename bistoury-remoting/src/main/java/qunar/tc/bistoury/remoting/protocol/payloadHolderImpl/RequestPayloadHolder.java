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
