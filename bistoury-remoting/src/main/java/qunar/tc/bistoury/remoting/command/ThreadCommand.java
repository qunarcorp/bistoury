package qunar.tc.bistoury.remoting.command;

/**
 * @author leix.xie
 * @date 2019/5/24 11:02
 * @describe
 */
public class ThreadCommand {
    //0-ALL_THREADS_INFO, 1-THREAD_DETAIL, 2-DUMP_THREADS, 3-DEADLOCK_THREAD
    private int type;
    private String pid;
    private int maxDepth;
    private long threadId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    @Override
    public String toString() {
        return "ThreadCommand{" +
                "type=" + type +
                ", pid=" + pid +
                ", threadId=" + threadId +
                '}';
    }
}
