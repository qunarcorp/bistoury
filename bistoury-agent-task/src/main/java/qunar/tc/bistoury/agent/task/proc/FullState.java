package qunar.tc.bistoury.agent.task.proc;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author cai.wen
 * @date 19-1-18
 */
public class FullState {

    public final CpuState cpuState;
    public final ProcessState processState;
    public final Map<Integer, ThreadState> threadInfo;

    public FullState(CpuState cpuState, ProcessState processState, Map<Integer, ThreadState> threadInfo) {
        this.cpuState = cpuState;
        this.processState = processState;
        this.threadInfo = ImmutableMap.copyOf(threadInfo);
    }

    @Override
    public String toString() {
        return "FullState{" +
                "cpuState=" + cpuState +
                ", processState=" + processState +
                ", threadInfo=" + threadInfo +
                '}';
    }
}
