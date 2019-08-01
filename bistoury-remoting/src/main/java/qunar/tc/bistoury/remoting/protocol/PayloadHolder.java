package qunar.tc.bistoury.remoting.protocol;

import io.netty.buffer.ByteBuf;

/**
 * @author leix.xie
 * @date 2019/5/13 15:11
 * @describe
 */
public interface PayloadHolder {
    void writeBody(ByteBuf out);
}
