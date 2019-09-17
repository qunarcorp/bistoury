package qunar.tc.bistoury.proxy.util;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import qunar.tc.bistoury.remoting.protocol.Datagram;

import java.net.InetSocketAddress;

/**
 * @author cai.wen created on 2019/9/17 10:48
 */
public class AgentIdUtil {

    private static final boolean initIdFromMessage = Boolean.valueOf(System.getProperty("agent.id.parse"));

    public static String getAgentId(Channel channel, Datagram message) {
        if (initIdFromMessage) {
            return getAgentIdFromBody(message.getBody());
        }
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        return address.getAddress().getHostAddress();
    }

    private static String getAgentIdFromBody(ByteBuf body) {
        int size = body.readableBytes();
        byte[] agentId = new byte[size];
        body.readBytes(agentId);
        return new String(agentId, Charsets.UTF_8);
    }
}
