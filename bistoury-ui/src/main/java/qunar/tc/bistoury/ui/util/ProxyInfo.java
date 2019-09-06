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

/**
 * @author leix.xie
 * @date 2019/9/4 12:05
 * @describe
 */
public class ProxyInfo {
    private String ip;
    private int tomcatPort;
    private int websocketPort;

    public ProxyInfo(String ip, int tomcatPort, int websocketPort) {
        this.ip = ip;
        this.tomcatPort = tomcatPort;
        this.websocketPort = websocketPort;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getTomcatPort() {
        return tomcatPort;
    }

    public void setTomcatPort(int tomcatPort) {
        this.tomcatPort = tomcatPort;
    }

    public int getWebsocketPort() {
        return websocketPort;
    }

    public void setWebsocketPort(int websocketPort) {
        this.websocketPort = websocketPort;
    }

    @Override
    public String toString() {
        return "ProxyInfo{" +
                "ip='" + ip + '\'' +
                ", tomcatPort=" + tomcatPort +
                ", websocketPort=" + websocketPort +
                '}';
    }
}
