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

package qunar.tc.bistoury.remoting.protocol;

import java.util.List;

/**
 * @author zhenyu.nie created on 2019 2019/5/16 14:38
 */
public class RequestData<T> {

    private String app;

    private int type;

    private List<AgentServerInfo> agentServerInfos;

    //前端反序列化会使用该字段
    private List<String> hosts;

    private T command;

    private String token;

    private String user;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<AgentServerInfo> getAgentServerInfos() {
        return agentServerInfos;
    }

    public void setAgentServerInfos(List<AgentServerInfo> agentServerInfos) {
        this.agentServerInfos = agentServerInfos;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public T getCommand() {
        return command;
    }

    public void setCommand(T command) {
        this.command = command;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static <U, V> RequestData<V> copyWithCommand(RequestData<U> input, V command) {
        RequestData<V> result = new RequestData<>();
        result.setApp(input.getApp());
        result.setType(input.getType());
        result.setAgentServerInfos(input.getAgentServerInfos());
        result.setHosts(input.getHosts());
        result.setCommand(command);
        result.setToken(input.getToken());
        result.setUser(input.getUser());
        return result;
    }

    @Override
    public String toString() {
        return "Request{" +
                "app='" + app + '\'' +
                ", type=" + type +
                ", agentServerInfos=" + agentServerInfos +
                ", command='" + command + '\'' +
                ", token='" + token + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
