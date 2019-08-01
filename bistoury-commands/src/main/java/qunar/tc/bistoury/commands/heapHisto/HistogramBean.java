package qunar.tc.bistoury.commands.heapHisto;

/**
 * @author: leix.xie
 * @date: 2019/4/1 10:42
 * @describe：
 */
public class HistogramBean {
    private long count;
    private long bytes;
    private String className;

    public HistogramBean() {

    }

    public HistogramBean(long count, long bytes, String className) {
        this.count = count;
        this.bytes = bytes;
        this.className = className;
    }

    public HistogramBean(String count, String bytes, String className) {
        this(Long.valueOf(count), Long.valueOf(bytes), className);
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "HistogramBean{" +
                "count=" + count +
                ", bytes=" + bytes +
                ", className='" + className + '\'' +
                '}';
    }
}
