package qunar.tc.bistoury.instrument.client.profiler.sync.runtime;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicLongMap;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggerHelper;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.profiler.compact.CompactClassHelper;
import qunar.tc.bistoury.common.profiler.method.MethodCache;
import qunar.tc.bistoury.common.profiler.method.MethodInfo;
import qunar.tc.bistoury.instrument.client.profiler.sync.Manager;
import qunar.tc.bistoury.instrument.client.profiler.sync.runtime.cpu.DumpData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author cai.wen created on 2019/10/17 11:15
 */
public class ProfilerDataDumper {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final Splitter METHOD_ID_SPLITTER = Splitter.on("-").omitEmptyStrings();

    private final ProfilerData profilerData;

    public ProfilerDataDumper() {
        profilerData = ProfilerData.getInstance();
    }

    public void dump() {
        logger.info("start dump");
        DumpData dumpData = profilerData.getDumpData();
        long dumpTime = System.currentTimeMillis();
        if (Manager.isDebugMode()) {
            logger.info("dumpData: {}", dumpData);
        }

        doDump(dumpData.getBlockedTimes(), Manager.getBlockedDataPath(), false);
        doDump(dumpData.getBlockedTimes(), Manager.getFilterBlockedDataPath(), true);
        doDump(dumpData.getRunnableCpuTimes(), Manager.getRunnableDataPath(), false);
        doDump(dumpData.getRunnableCpuTimes(), Manager.getFilterRunnableDataPath(), true);
        doDump(dumpData.getTimedWaitingTimes(), Manager.getTimedWaitingDataPath(), false);
        doDump(dumpData.getTimedWaitingTimes(), Manager.getFilterTimedWaitingDataPath(), true);
        doDump(dumpData.getWaitingTimes(), Manager.getWaitingDataPath(), false);
        doDump(dumpData.getWaitingTimes(), Manager.getFilterWaitingDataPath(), true);

        dumpAllState(dumpData);
        Manager.renameResult(dumpTime);
    }

    private void dumpAllState(DumpData dumpData) {
        AtomicLongMap<Integer> allStateMap = AtomicLongMap.create();
        List<Map<Integer, Long>> allRecord = ImmutableList.of(dumpData.getBlockedTimes(), dumpData.getRunnableCpuTimes(),
                dumpData.getTimedWaitingTimes(), dumpData.getWaitingTimes());
        for (Map<Integer, Long> cpuTime : allRecord) {
            for (Map.Entry<Integer, Long> entry : cpuTime.entrySet()) {
                allStateMap.addAndGet(entry.getKey(), entry.getValue());
            }
        }
        doDump(allStateMap.asMap(), Manager.getAllStatePath(), false);
        doDump(allStateMap.asMap(), Manager.getFilterAllStatePath(), true);
    }

    private void doDump(Map<Integer, Long> cpuTimes, String dumpFile, boolean isFilter) {
        File realPath = new File(dumpFile);

        try {
            realPath.createNewFile();
        } catch (IOException e) {
            logger.error("", BistouryLoggerHelper.formatMessage("create dump file error. path: {}", realPath), e);
        }

        List<Map.Entry<Integer, Long>> timeList;
        if (Manager.isDebugMode()) {
            timeList = sortByValue(cpuTimes);
        } else {
            timeList = Lists.newArrayList(cpuTimes.entrySet());
        }

        try (BufferedWriter dumpStream = new BufferedWriter(new FileWriter(realPath))) {
            Map<Integer, String> callStackIds = profilerData.getCallStackMapping();
            for (Map.Entry<Integer, Long> entry : timeList) {
                String callStackTags = callStackIds.get(entry.getKey());
                String callStack = getCallStack(callStackTags, isFilter);
                Long cpuTime = entry.getValue();
                if (callStack.isEmpty()) {
                    continue;
                }
                if (cpuTime == 0) {
                    continue;
                }
                dumpStream.write(callStack);
                dumpStream.write(" ");
                dumpStream.write(cpuTime.toString());
                dumpStream.write("\n");
            }
            dumpStream.flush();
        } catch (Exception e) {
            logger.error("", BistouryLoggerHelper.formatMessage("dump cputime map error. dump file: {}. path: {}", dumpFile), e);
        }
    }

    private String getCallStack(String methodIdTag, boolean isFilter) {
        List<String> methodIdStr = METHOD_ID_SPLITTER.splitToList(methodIdTag);
        List<Integer> methodIds = Lists.newArrayListWithExpectedSize(methodIdStr.size());
        for (String idStr : methodIdStr) {
            methodIds.add(Integer.valueOf(idStr));
        }

        StringBuilder builder = new StringBuilder();
        List<MethodInfo> methodInfos = isFilter ? getFilterMethodInfos(methodIds) : getMethodInfos(methodIds);
        if (methodInfos.size() <= 1) {
            return "";
        }
        for (MethodInfo methodInfo : methodInfos) {
            builder.append(methodInfo).append(";");
        }
        if (builder.length() != 0) {
            builder.delete(builder.length() - 1, builder.length());
        }

        return builder.toString();
    }

    private List<MethodInfo> getMethodInfos(List<Integer> methodIds) {
        Map<Integer, MethodInfo> idMap = MethodCache.getIdToInfoMapping();
        List<MethodInfo> result = Lists.newArrayListWithExpectedSize(methodIds.size());
        MethodInfo preInfo = null;
        for (int methodId : Lists.reverse(methodIds)) {
            MethodInfo methodInfo = idMap.get(methodId);

            //如果当前方法和上一层是重载,抛弃
            if (methodInfo.equals(preInfo)) {
                continue;
            }
            result.add(methodInfo);
            preInfo = methodInfo;
        }
        return result;
    }

    private List<MethodInfo> getFilterMethodInfos(List<Integer> methodIds) {
        Map<Integer, MethodInfo> idMap = MethodCache.getIdToInfoMapping();
        List<MethodInfo> result = Lists.newArrayListWithExpectedSize(methodIds.size() / 2);
        List<Integer> reverseIds = Lists.reverse(methodIds);

        MethodInfo firstMethodInfo = idMap.get(reverseIds.remove(0));
        //最底层的一般是Thread的子类,直接打印
        result.add(firstMethodInfo);

        boolean isPreCompact = CompactClassHelper.isCompactClass(firstMethodInfo.getClassName());
        MethodInfo preInfo = null;
        for (int methodId : reverseIds) {
            MethodInfo methodInfo = idMap.get(methodId);

            //如果当前方法和上一层是重载,抛弃
            if (methodInfo.equals(preInfo)) {
                continue;
            }
            preInfo = methodInfo;

            boolean isCompact = CompactClassHelper.isCompactClass(methodInfo.getClassName());
            if (isPreCompact && isCompact) {
                continue;
            }
            isPreCompact = isCompact;

            result.add(methodInfo);
        }

        if (isPreCompact && result.size() == 2) {
            return ImmutableList.of();
        }

        return result;
    }

    private List<Map.Entry<Integer, Long>> sortByValue(Map<Integer, Long> countMap) {
        List<Map.Entry<Integer, Long>> countEntryList = new ArrayList<>(countMap.entrySet());
        Collections.sort(countEntryList, new Comparator<Map.Entry<Integer, Long>>() {
            @Override
            public int compare(Map.Entry<Integer, Long> l, Map.Entry<Integer, Long> r) {
                long left = l.getValue();
                long right = r.getValue();
                if (left > right) {
                    return -1;
                } else if (left == right) {
                    return 0;
                }
                return 1;
            }
        });
        return countEntryList;
    }
}
