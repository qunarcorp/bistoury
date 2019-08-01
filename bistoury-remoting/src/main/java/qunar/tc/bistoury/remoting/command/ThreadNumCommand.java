package qunar.tc.bistoury.remoting.command;

/**
 * @author zhenyu.nie created on 2019 2019/1/15 11:03
 */
public class ThreadNumCommand {

    private String start;

    private String end;

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "ThreadNumCommand{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
