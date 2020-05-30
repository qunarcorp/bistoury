package qunar.tc.bistoury.instrument.client.profiler.sync.runtime;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import qunar.tc.bistoury.common.profiler.method.MethodCache;
import qunar.tc.bistoury.common.profiler.method.MethodInfo;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.sync.runtime.cpu.DumpData;
import qunar.tc.bistoury.instrument.client.profiler.sync.runtime.cpu.ThreadCpuInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cai.wen created on 2019/10/21 14:41
 */
public class ProfilerData {

    private ProfilerData() {
    }

    private static ProfilerData INSTANCE = new ProfilerData();

    private final Map<String, Integer> callStackIds = new HashMap<>();

    private final AtomicInteger stackIdGen = new AtomicInteger();

    private volatile DumpData preDumpData = new DumpData(new HashMap<Long, Long>());

    void addStackTrace(ThreadCpuInfo threadCpuInfo, DumpData curDumpData) {
        List<StackTraceElement> elements = Arrays.asList(threadCpuInfo.getThreadInfo().getStackTrace());
        int[] methodIds = getMethodIds(elements);
        if (methodIds.length == 0) {
            return;
        }

        String callStackKey = getCallStackKey(methodIds);
        int callStackId = getStackId(callStackKey);
        doAddStackTraceData(threadCpuInfo.getThreadInfo().getThreadId(), callStackId,
                threadCpuInfo.getCpuTime(), threadCpuInfo.getThreadInfo().getThreadState(), curDumpData);
    }

    Map<Integer, String> getCallStackMapping() {
        Map<String, Integer> temp = ImmutableMap.copyOf(callStackIds);
        Map<Integer, String> result = Maps.newHashMapWithExpectedSize(temp.size());
        for (Map.Entry<String, Integer> idEntry : temp.entrySet()) {
            result.put(idEntry.getValue(), idEntry.getKey());
        }
        return result;
    }

    private void doAddStackTraceData(long threadId, int callStackId, long cpuTime, Thread.State state, DumpData dumpData) {
        Map<Integer, Long> times = getTimeMappingForState(state, dumpData);
        if (times == null) {
            return;
        }
        times.put(callStackId, getTime(state, threadId, cpuTime, callStackId, times));
    }

    private long getTime(Thread.State state, long threadId, long cpuTime, int callStackId, Map<Integer, Long> times) {
        if (state != Thread.State.RUNNABLE) {
            return AgentProfilerContext.getIntervalNs();
        }
        //获取上一次采样时,线程对应的cpu time
        Long preCpuTime = preDumpData.getThreadCpuTimes().get(threadId);
        preCpuTime = preCpuTime == null ? 0 : preCpuTime;

        //获取这次采样和上次采样之间,当前线程消耗的cpu time
        long realCpuTime = cpuTime - preCpuTime;

        //把两次采样间,线程消耗的时间,增加到对应的map中去
        Long preCallStackTime = times.get(callStackId);
        preCallStackTime = preCallStackTime == null ? 0 : preCallStackTime;
        return realCpuTime + preCallStackTime;
    }

    private Map<Integer, Long> getTimeMappingForState(Thread.State state, DumpData dumpData) {
        switch (state) {
            case RUNNABLE:
                return dumpData.getRunnableCpuTimes();
            case WAITING:
                return dumpData.getWaitingTimes();
            case BLOCKED:
                return dumpData.getBlockedTimes();
            case TIMED_WAITING:
                return dumpData.getTimedWaitingTimes();
            default:
                return null;
        }
    }

    void setDumpData(DumpData dumpData) {
        preDumpData = dumpData;
    }

    DumpData getDumpData() {
        return preDumpData;
    }

    private String getCallStackKey(int[] methodIds) {
        StringBuilder builder = new StringBuilder();
        for (int methodId : methodIds) {
            builder.append(methodId).append("-");
        }
        if (builder.length() != 0) {
            builder.delete(builder.length() - 1, builder.length());
        }
        return builder.toString();
    }

    private int getStackId(String callStackKey) {
        Integer stackId = callStackIds.get(callStackKey);
        if (stackId == null) {
            stackId = stackIdGen.incrementAndGet();
        }
        callStackIds.put(callStackKey, stackId);
        return stackId;
    }

    private int[] getMethodIds(List<StackTraceElement> stackTraceElements) {
        int[] methodIds = new int[stackTraceElements.size()];

        for (int i = 0; i < stackTraceElements.size(); i++) {
            StackTraceElement element = stackTraceElements.get(i);
            MethodInfo methodInfo = new MethodInfo(element.getClassName(), element.getMethodName());
            methodIds[i] = MethodCache.getMethodTagId(methodInfo);
        }
        return methodIds;
    }

    public synchronized static ProfilerData getInstance() {
        return INSTANCE;
    }

    public static synchronized void reset() {
        INSTANCE = new ProfilerData();
    }
}
