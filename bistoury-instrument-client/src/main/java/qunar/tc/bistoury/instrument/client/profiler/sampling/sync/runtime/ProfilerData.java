package qunar.tc.bistoury.instrument.client.profiler.sampling.sync.runtime;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import qunar.tc.bistoury.instrument.client.profiler.sampling.sync.runtime.cpu.DumpData;
import qunar.tc.bistoury.instrument.client.profiler.sampling.sync.runtime.cpu.ThreadCpuInfo;
import qunar.tc.bistoury.instrument.client.profiler.sampling.sync.runtime.method.MethodCache;
import qunar.tc.bistoury.instrument.client.profiler.sampling.sync.runtime.method.MethodInfo;

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
        Map<Integer, Long> cpuTimes = getCpuTimeForState(state, dumpData);
        if (cpuTimes == null) {
            return;
        }

        //获取上一次采样时,线程对应的cpu time
        Long preCpuTime = preDumpData.getThreadCpuTimes().get(threadId);
        preCpuTime = preCpuTime == null ? 0 : preCpuTime;

        //获取这次采样和上次采样之间,当前线程消耗的cpu time
        long realCpuTime = cpuTime - preCpuTime;

        //把两次采样间,线程消耗的时间,增加到对应的map中去
        Long preCallStackTime = cpuTimes.get(callStackId);
        preCallStackTime = preCallStackTime == null ? 0 : preCallStackTime;
        cpuTimes.put(callStackId, realCpuTime + preCallStackTime);
    }

    private Map<Integer, Long> getCpuTimeForState(Thread.State state, DumpData dumpData) {
        switch (state) {
            case RUNNABLE:
                return dumpData.getRunnableCpuTime();
            case WAITING:
                return dumpData.getWaitingCpuTime();
            case BLOCKED:
                return dumpData.getBlockedCpuTime();
            case TIMED_WAITING:
                return dumpData.getTimedWaitingCpuTime();
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
            methodIds[i] = MethodCache.addMethod(methodInfo);
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
