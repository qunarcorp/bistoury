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
