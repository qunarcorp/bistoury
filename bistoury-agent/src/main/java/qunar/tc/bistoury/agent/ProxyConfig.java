package qunar.tc.bistoury.agent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author zhenyu.nie created on 2018 2018/10/25 19:09
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ProxyConfig {

    private String ip;

    private int port;

    private int heartbeatSec;

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

    public int getHeartbeatSec() {
        return heartbeatSec;
    }

    public void setHeartbeatSec(int heartbeatSec) {
        this.heartbeatSec = heartbeatSec;
    }

    @Override
    public String toString() {
        return "ProxyConfig{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", heartbeatSec=" + heartbeatSec +
                '}';
    }
}
