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

package qunar.tc.bistoury.application.api.pojo;

/**
 * @author leix.xie
 * @date 2019/7/2 14:55
 * @describe
 */
public class AppServer {
    private String serverId;
    private String ip;
    private int port;
    private String host;
    private String logDir;
    private String room;
    private String appCode;
    private boolean autoJStackEnable = false;
    private boolean autoJMapHistoEnable = false;

    public AppServer() {

    }

    public AppServer(String serverId, String ip, int port, String host, String logDir, String room, String appCode) {
        this.serverId = serverId;
        this.ip = ip;
        this.port = port;
        this.host = host;
        this.logDir = logDir;
        this.room = room;
        this.appCode = appCode;
    }

    public AppServer(String serverId, String ip, int port, String host, String logDir, String room, String appCode, boolean autoJStackEnable, boolean autoJMapHistoEnable) {
        this.serverId = serverId;
        this.ip = ip;
        this.port = port;
        this.host = host;
        this.logDir = logDir;
        this.room = room;
        this.appCode = appCode;
        this.autoJStackEnable = autoJStackEnable;
        this.autoJMapHistoEnable = autoJMapHistoEnable;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public boolean isAutoJStackEnable() {
        return autoJStackEnable;
    }

    public void setAutoJStackEnable(boolean autoJStackEnable) {
        this.autoJStackEnable = autoJStackEnable;
    }

    public boolean isAutoJMapHistoEnable() {
        return autoJMapHistoEnable;
    }

    public void setAutoJMapHistoEnable(boolean autoJMapHistoEnable) {
        this.autoJMapHistoEnable = autoJMapHistoEnable;
    }

    @Override
    public String toString() {
        return "AppServer{" +
                "serverId='" + serverId + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", host='" + host + '\'' +
                ", logDir='" + logDir + '\'' +
                ", room='" + room + '\'' +
                ", appCode='" + appCode + '\'' +
                ", autoJStackEnable=" + autoJStackEnable +
                ", autoJMapHistoEnable=" + autoJMapHistoEnable +
                '}';
    }
}
