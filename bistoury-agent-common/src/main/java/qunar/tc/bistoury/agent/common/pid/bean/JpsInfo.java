package qunar.tc.bistoury.agent.common.pid.bean;

/**
 * @author: leix.xie
 * @date: 2019/3/13 17:16
 * @describe：
 */
public class JpsInfo {
    private int pid;
    private String clazz;

    public JpsInfo(int pid, String clazz) {
        this.clazz = clazz;
        this.pid = pid;
    }

    public String getClazz() {
        return clazz;
    }

    public int getPid() {
        return pid;
    }
}
