package qunar.tc.bistoury.proxy.communicate;

import io.netty.channel.ChannelHandlerContext;
import qunar.tc.bistoury.remoting.protocol.Datagram;

import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/5/14 17:15
 */
public interface MessageProcessor {

    Set<Integer> codes();

    void process(ChannelHandlerContext ctx, Datagram message);
}
