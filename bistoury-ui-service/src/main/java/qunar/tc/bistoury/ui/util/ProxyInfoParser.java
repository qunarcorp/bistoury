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

package qunar.tc.bistoury.ui.util;

import com.google.common.base.Splitter;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author leix.xie
 * @date 2019/9/4 11:59
 * @describe
 */
public class ProxyInfoParser {
    private static final String COLON = ":";
    private static final Splitter PROXY_SPLITTER = Splitter.on(COLON);

    public static Optional<ProxyInfo> parseProxyInfo(String line) {
        List<String> list = PROXY_SPLITTER.splitToList(line);
        if (CollectionUtils.isEmpty(list) || list.size() != 3) {
            return Optional.empty();
        }
        final String ip = list.get(0);
        final int tomcatPort = Integer.valueOf(list.get(1));
        final int websocketPort = Integer.valueOf(list.get(2));
        return Optional.of(new ProxyInfo(ip, tomcatPort, websocketPort));
    }

    public static Optional<ProxyInfo> parseProxyInfoWithoutTomcatPort(String infoWithoutWebsocketPort) {
        List<String> list = PROXY_SPLITTER.splitToList(infoWithoutWebsocketPort);
        if (CollectionUtils.isEmpty(list) || list.size() != 2) {
            return Optional.empty();
        }
        final String ip = list.get(0);
        final int tomcatPort = Integer.valueOf(list.get(1));
        return Optional.of(new ProxyInfo(ip, tomcatPort, 0));
    }
}
