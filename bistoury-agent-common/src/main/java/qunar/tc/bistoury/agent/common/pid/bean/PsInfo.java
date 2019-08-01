package qunar.tc.bistoury.agent.common.pid.bean;

import java.util.Arrays;

/**
 * @author: leix.xie
 * @date: 2019/3/13 17:17
 * @describe：
 */
public class PsInfo {
    private String user;
    private int pid;
    private String command;
    private String[] params;

    public PsInfo(String user, int pid, String command, String[] params) {
        this.user = user;
        this.pid = pid;
        this.command = command;
        this.params = params;
    }

    public String getUser() {
        return user;
    }

    public int getPid() {
        return pid;
    }

    public String getCommand() {
        return command;
    }

    public String[] getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "PsInfo{" +
                "user='" + user + '\'' +
                ", pid=" + pid +
                ", command='" + command + '\'' +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}
