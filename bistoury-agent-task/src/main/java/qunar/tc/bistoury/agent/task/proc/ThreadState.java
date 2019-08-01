package qunar.tc.bistoury.agent.task.proc;

import java.util.List;

/**
 * @author cai.wen
 * @date 19-1-17
 */
class ThreadState {

    public final int tid;
    public final char state;
    public final long userTime;
    public final long systemTime;

    private ThreadState(int tid, char state, long userTime, long systemTime) {
        this.tid = tid;
        this.state = state;
        this.userTime = userTime;
        this.systemTime = systemTime;
    }

    public long totalTime() {
        return userTime + systemTime;
    }

    static ThreadState parse(List<String> info) {
        return new ThreadState(
                Integer.valueOf(info.get(0)),
                info.get(2).charAt(0),
                Long.valueOf(info.get(13)),
                Long.valueOf(info.get(14)));
    }

    @Override
    public String toString() {
        return "ThreadState{" +
                "tid=" + tid +
                ", state=" + state +
                ", userTime=" + userTime +
                ", systemTime=" + systemTime +
                '}';
    }
}
