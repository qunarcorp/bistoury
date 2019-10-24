package qunar.tc.bistoury.instrument.client.profiler.sampling.runtime.cpu;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cai.wen created on 2019/10/21 14:35
 */
public class DumpData {

    private Map<Integer, Long> runnableCpuTime = new HashMap<>();

    private Map<Integer, Long> blockedCpuTime = new HashMap<>();

    private Map<Integer, Long> waitingCpuTime = new HashMap<>();

    private Map<Integer, Long> timedWaitingCpuTime = new HashMap<>();

    private final Map<Long, Long> threadCpuTimes;

    public DumpData(Map<Long, Long> threadCpuTimes) {
        this.threadCpuTimes = threadCpuTimes;
    }

    public DumpData(Map<Integer, Long> runnableCpuTime, Map<Integer, Long> blockedCpuTime,
                    Map<Integer, Long> waitingCpuTime, Map<Integer, Long> timedWaitingCpuTime,
                    Map<Long, Long> threadCpuTimes) {
        this.runnableCpuTime = Maps.newHashMap(runnableCpuTime);
        this.blockedCpuTime = Maps.newHashMap(blockedCpuTime);
        this.waitingCpuTime = Maps.newHashMap(waitingCpuTime);
        this.timedWaitingCpuTime = Maps.newHashMap(timedWaitingCpuTime);
        this.threadCpuTimes = Maps.newHashMap(threadCpuTimes);
    }

    public Map<Integer, Long> getRunnableCpuTime() {
        return runnableCpuTime;
    }

    public Map<Integer, Long> getBlockedCpuTime() {
        return blockedCpuTime;
    }

    public Map<Integer, Long> getWaitingCpuTime() {
        return waitingCpuTime;
    }

    public Map<Integer, Long> getTimedWaitingCpuTime() {
        return timedWaitingCpuTime;
    }

    public Map<Long, Long> getThreadCpuTimes() {
        return threadCpuTimes;
    }

    @Override
    public String toString() {
        return "DumpData{" +
                "runnableCpuTime=" + runnableCpuTime +
                ", blockedCpuTime=" + blockedCpuTime +
                ", waitingCpuTime=" + waitingCpuTime +
                ", timedWaitingCpuTime=" + timedWaitingCpuTime +
                ", threadCpuTimes=" + threadCpuTimes +
                '}';
    }
}
