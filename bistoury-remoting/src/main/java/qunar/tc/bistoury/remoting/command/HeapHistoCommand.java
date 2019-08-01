package qunar.tc.bistoury.remoting.command;

/**
 * @author leix.xie
 * @date 2019/5/27 10:58
 * @describe
 */
public class HeapHistoCommand {
    private String param;
    private Long timestamp;
    private String pid;

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "HeapHistoCommand{" +
                "param='" + param + '\'' +
                ", timestamp=" + timestamp +
                ", pid='" + pid + '\'' +
                '}';
    }
}
