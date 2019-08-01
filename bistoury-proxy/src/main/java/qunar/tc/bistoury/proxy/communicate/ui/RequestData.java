package qunar.tc.bistoury.proxy.communicate.ui;

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
