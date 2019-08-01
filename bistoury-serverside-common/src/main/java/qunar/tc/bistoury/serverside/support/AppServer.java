package qunar.tc.bistoury.serverside.support;

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
