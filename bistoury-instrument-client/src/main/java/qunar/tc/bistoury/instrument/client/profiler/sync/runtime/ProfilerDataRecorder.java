package qunar.tc.bistoury.instrument.client.profiler.sync.runtime;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import qunar.tc.bistoury.instrument.client.profiler.sync.runtime.cpu.DumpData;
import qunar.tc.bistoury.instrument.client.profiler.sync.runtime.cpu.ThreadCpuInfo;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.Map;

/**
 * @author cai.wen created on 2019/10/23 20:35
 */
public class ProfilerDataRecorder {

    private final ProfilerData profilerData;

    public ProfilerDataRecorder() {
        this.profilerData = ProfilerData.getInstance();
    }

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public void record() {
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        DumpData dumpData = getCurrentDumpData(threadInfos);
        try {
            if (isFirstDump()) {
                return;
            }
            doRecordStackTraceData(threadInfos, dumpData);
        } finally {
            profilerData.setDumpData(dumpData);
        }
    }

    private boolean isFirstDump() {
        return profilerData.getDumpData().getThreadCpuTimes().isEmpty();
    }

    private void doRecordStackTraceData(ThreadInfo[] threadInfos, DumpData dumpData) {
        List<ThreadCpuInfo> threadCpuInfos = Lists.newArrayList();
        for (ThreadInfo threadInfo : threadInfos) {

            if (threadInfo.getStackTrace().length == 0) {
                continue;
            }

            Long cpuTime = dumpData.getThreadCpuTimes().get(threadInfo.getThreadId());
            if (cpuTime == null) {
                continue;
            }
            threadCpuInfos.add(new ThreadCpuInfo(threadInfo, cpuTime));
        }

        for (ThreadCpuInfo threadCpuInfo : threadCpuInfos) {
            profilerData.addStackTrace(threadCpuInfo, dumpData);
        }
    }


    private DumpData getCurrentDumpData(ThreadInfo[] threadInfos) {
        Map<Long, Long> threadCpuTimes = Maps.newHashMapWithExpectedSize(threadInfos.length);
        for (ThreadInfo threadInfo : threadInfos) {
            long threadId = threadInfo.getThreadId();
            long threadCpuTime = threadMXBean.getThreadCpuTime(threadId);
            if (threadCpuTime == -1) {
                continue;
            }
            threadCpuTimes.put(threadId, threadCpuTime);
        }
        DumpData preDumpData = profilerData.getDumpData();
        return new DumpData(preDumpData.getRunnableCpuTimes(), preDumpData.getBlockedTimes(), preDumpData.getWaitingTimes(),
                preDumpData.getTimedWaitingTimes(), threadCpuTimes);
    }
}
