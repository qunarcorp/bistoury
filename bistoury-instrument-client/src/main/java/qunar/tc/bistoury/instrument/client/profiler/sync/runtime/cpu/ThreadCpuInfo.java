package qunar.tc.bistoury.instrument.client.profiler.sync.runtime.cpu;

import java.lang.management.ThreadInfo;

/**
 * @author cai.wen created on 2019/10/18 19:18
 */
public class ThreadCpuInfo {

    private final ThreadInfo threadInfo;

    private final long cpuTime;

    public ThreadCpuInfo(ThreadInfo threadInfo, long cpuTime) {
        this.threadInfo = threadInfo;
        this.cpuTime = cpuTime;
    }

    public ThreadInfo getThreadInfo() {
        return threadInfo;
    }

    public long getCpuTime() {
        return cpuTime;
    }
}
