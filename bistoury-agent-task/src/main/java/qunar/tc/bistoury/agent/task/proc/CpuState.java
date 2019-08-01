package qunar.tc.bistoury.agent.task.proc;

import java.util.List;

/**
 * @author cai.wen
 * @date 19-1-17
 */
class CpuState {

    public final long userTime;
    public final long niceTime;
    public final long systemTime;
    public final long idleTime;
    public final long ioWaitTime;
    public final long irqTime;
    public final long softIrqTime;
    //public final long stealStolen;
    //public final long guest;

    private CpuState(long userTime, long niceTime, long systemTime, long idleTime, long ioWaitTime, long irqTime, long softIrqTime) {
        this.userTime = userTime;
        this.niceTime = niceTime;
        this.systemTime = systemTime;
        this.idleTime = idleTime;
        this.ioWaitTime = ioWaitTime;
        this.irqTime = irqTime;
        this.softIrqTime = softIrqTime;
    }

    public long totalTime() {
        return userTime + niceTime + systemTime + idleTime + ioWaitTime + irqTime + softIrqTime;
    }

    static CpuState parse(List<String> info) {
        return new CpuState(Long.valueOf(info.get(1)),
                Long.valueOf(info.get(2)),
                Long.valueOf(info.get(3)),
                Long.valueOf(info.get(4)),
                Long.valueOf(info.get(5)),
                Long.valueOf(info.get(6)),
                Long.valueOf(info.get(7)));
    }

    @Override
    public String toString() {
        return "CpuState{" +
                "userTime=" + userTime +
                ", niceTime=" + niceTime +
                ", systemTime=" + systemTime +
                ", idleTime=" + idleTime +
                ", ioWaitTime=" + ioWaitTime +
                ", irqTime=" + irqTime +
                ", softIrqTime=" + softIrqTime +
                '}';
    }
}
