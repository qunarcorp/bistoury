package qunar.tc.bistoury.proxy.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.protocol.Datagram;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author cai.wen created on 2019/9/17 10:48
 */
public class AgentIdUtil {

    private static final TypeReference<Map<String, String>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, String>>() {};

    public static String getAgentId(Channel channel, Datagram message) {
        ByteBuf body = message.getBody();
        if (body == null || body.readableBytes() == 0) {
            InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
            return address.getAddress().getHostAddress();
        } else {
            return getAgentIdFromBody(body);
        }
    }

    private static String getAgentIdFromBody(ByteBuf body) {
        int size = body.readableBytes();
        byte[] agentInfoBytes = new byte[size];
        body.readBytes(agentInfoBytes);
        Map<String, String> agentInfo = JacksonSerializer.deSerialize(agentInfoBytes, MAP_TYPE_REFERENCE);
        String agentId = agentInfo.get(BistouryConstants.AGENT_ID_NAME);
        if (Strings.isNullOrEmpty(agentId)) {
            throw new IllegalArgumentException("illegal heartbeat");
        } else {
            return agentId;
        }
    }
}
