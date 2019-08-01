package qunar.tc.bistoury.agent.task.proc;

import java.util.List;

/**
 * @author cai.wen
 * @date 19-1-17
 */
class ProcessState {

    public final int pid;
    public final int ppid;
    public final char state;
    public final String command;
    public final int threadNum;
    public final long userTime;
    public final long systemTime;
    /**
     * Amount of time that this process's waited-for children have been scheduled in user mode
     */
    public final long cUserTime;
    /**
     * Amount of time that this process's waited-for children have been scheduled in kernel mode
     */
    public final long cSystemTime;
    //private long start_time;

    private ProcessState(int pid, String command, char state, int ppid, long userTime, long systemTime, long cUserTime, long cSystemTime, int threadNum) {
        this.pid = pid;
        this.ppid = ppid;
        this.state = state;
        this.command = command;
        this.threadNum = threadNum;
        this.userTime = userTime;
        this.systemTime = systemTime;
        this.cUserTime = cUserTime;
        this.cSystemTime = cSystemTime;
    }

    public long totalTime() {
        return userTime + systemTime + cUserTime + cSystemTime;
    }

    static ProcessState parse(List<String> info) {
        return new ProcessState(
                Integer.valueOf(info.get(0)),
                info.get(1),
                info.get(2).charAt(0),
                Integer.valueOf(info.get(3)),
                Long.valueOf(info.get(13)),
                Long.valueOf(info.get(14)),
                Long.valueOf(info.get(15)),
                Long.valueOf(info.get(16)),
                Integer.valueOf(info.get(19))
        );
    }

    @Override
    public String toString() {
        return "ProcessState{" +
                "pid=" + pid +
                ", ppid=" + ppid +
                ", state=" + state +
                ", command='" + command + '\'' +
                ", threadNum=" + threadNum +
                ", userTime=" + userTime +
                ", systemTime=" + systemTime +
                ", cUserTime=" + cUserTime +
                ", cSystemTime=" + cSystemTime +
                '}';
    }
}
