package qunar.tc.bistoury.instrument.client.profiler.sync.runtime.cpu;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cai.wen created on 2019/10/21 14:35
 */
public class DumpData {

    private Map<Integer, Long> runnableCpuTimes = new HashMap<>();

    private Map<Integer, Long> blockedTimes = new HashMap<>();

    private Map<Integer, Long> waitingTimes = new HashMap<>();

    private Map<Integer, Long> timedWaitingTimes = new HashMap<>();

    private final Map<Long, Long> threadCpuTimes;

    public DumpData(Map<Long, Long> threadCpuTimes) {
        this.threadCpuTimes = threadCpuTimes;
    }

    public DumpData(Map<Integer, Long> runnableCpuTimes, Map<Integer, Long> blockedTimes,
                    Map<Integer, Long> waitingTimes, Map<Integer, Long> timedWaitingTimes,
                    Map<Long, Long> threadCpuTimes) {
        this.runnableCpuTimes = Maps.newHashMap(runnableCpuTimes);
        this.blockedTimes = Maps.newHashMap(blockedTimes);
        this.waitingTimes = Maps.newHashMap(waitingTimes);
        this.timedWaitingTimes = Maps.newHashMap(timedWaitingTimes);
        this.threadCpuTimes = Maps.newHashMap(threadCpuTimes);
    }

    public Map<Integer, Long> getRunnableCpuTimes() {
        return runnableCpuTimes;
    }

    public Map<Integer, Long> getBlockedTimes() {
        return blockedTimes;
    }

    public Map<Integer, Long> getWaitingTimes() {
        return waitingTimes;
    }

    public Map<Integer, Long> getTimedWaitingTimes() {
        return timedWaitingTimes;
    }

    public Map<Long, Long> getThreadCpuTimes() {
        return threadCpuTimes;
    }

    @Override
    public String toString() {
        return "DumpData{" +
                "runnableCpuTimes=" + runnableCpuTimes +
                ", blockedTimes=" + blockedTimes +
                ", waitingTimes=" + waitingTimes +
                ", timedWaitingTimes=" + timedWaitingTimes +
                ", threadCpuTimes=" + threadCpuTimes +
                '}';
    }
}
