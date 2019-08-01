package qunar.tc.bistoury.remoting.protocol.payloadHolderImpl;

import io.netty.buffer.ByteBuf;
import qunar.tc.bistoury.remoting.protocol.PayloadHolder;

/**
 * @author leix.xie
 * @date 2019/5/27 11:28
 * @describe
 */
public class ResponsePayloadHolder implements PayloadHolder {

    private byte[] data;

    public ResponsePayloadHolder(byte[] data) {
        this.data = data;
    }

    @Override
    public void writeBody(ByteBuf out) {
        out.writeBytes(data);
    }
}
