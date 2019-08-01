package qunar.tc.bistoury.remoting.command;

/**
 * @author zhenyu.nie created on 2019 2019/1/9 21:13
 */
public class CpuTimeCommand {

    private String threadId;

    private String start;

    private String end;

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "CpuTimeCommand{" +
                "threadId='" + threadId + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
