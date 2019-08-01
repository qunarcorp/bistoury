package qunar.tc.bistoury.remoting.command;

/**
 * @author: leix.xie
 * @date: 2019/1/9 15:25
 * @describe：
 */
public class MonitorCommand {
    private String type;
    private String name;
    private Long startTime;
    private Long endTime;
    private Long query;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getQuery() {
        return query;
    }

    public void setQuery(Long query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "MonitorCommand{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", query=" + query +
                '}';
    }
}
