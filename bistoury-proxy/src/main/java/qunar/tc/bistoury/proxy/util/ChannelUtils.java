package qunar.tc.bistoury.proxy.util;

import com.google.common.net.InetAddresses;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 13:50
 */
public class ChannelUtils {
    private static final String LOCALHOST = "localhost";
    private static final int LOCALHOST_IP = InetAddresses.coerceToInteger(InetAddresses.forString("127.0.0.1"));

    public static String getIp(Channel channel) {
        InetSocketAddress socket = (InetSocketAddress) (channel.remoteAddress());
        return socket.getAddress().getHostAddress();
    }

    public static int getIpToN(Channel channel) {
        String ip = getIp(channel);
        return inetAtoN(ip);
    }

    public static int inetAtoN(String ip) {
        if (ip.equalsIgnoreCase(LOCALHOST)) return LOCALHOST_IP;
        return InetAddresses.coerceToInteger(InetAddresses.forString(ip));
    }

    public static String inetNtoA(int ip) {
        return InetAddresses.toAddrString(InetAddresses.fromInteger(ip));
    }

}
