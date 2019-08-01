package qunar.tc.bistoury.proxy.communicate.ui;

import java.io.Serializable;

/**
 * @author leix.xie
 * @date 2019/5/22 15:44
 * @describe
 */
public class AgentServerInfo implements Serializable {
    private String agentId;
    private String host;
    private String ip;
    private String appcode;
    private int port;
    private String logdir;


    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAppcode() {
        return appcode;
    }

    public void setAppcode(String appcode) {
        this.appcode = appcode;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogdir() {
        return logdir;
    }

    public void setLogdir(String logdir) {
        this.logdir = logdir;
    }

    @Override
    public String toString() {
        return "AgentServerInfo{" +
                "agentId='" + agentId + '\'' +
                ", host='" + host + '\'' +
                ", ip='" + ip + '\'' +
                ", appcode='" + appcode + '\'' +
                ", port=" + port +
                ", logdir='" + logdir + '\'' +
                '}';
    }
}
