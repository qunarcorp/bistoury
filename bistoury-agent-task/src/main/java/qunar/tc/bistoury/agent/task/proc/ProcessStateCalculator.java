/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.agent.task.proc;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author cai.wen
 * @date 19-1-17
 */
public class ProcessStateCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessStateCalculator.class);

    private static final ProcessStateCalculator INSTANCE = new ProcessStateCalculator();

    private AtomicReference<FullState> lastMomentProcessState = new AtomicReference<>();
    private AtomicReference<FullState> lastMinuteProcessState = new AtomicReference<>();

    private ProcessStateCalculator() {

    }

    public Map<Integer, Double> threadCpuMinuteUsage(int pid) {
        FullState fullState = getCurrentFullState(pid);
        return threadCpuMomentUsage(lastMinuteProcessState, fullState);
    }

    public void startRecordFullStat(int pid) {
        threadCpuMomentUsage(pid);
    }

    public Map<Integer, Double> endRecordFullStat(int pid) {
        return threadCpuMomentUsage(pid);
    }

    private Map<Integer, Double> threadCpuMomentUsage(int pid) {
        FullState fullState = getCurrentFullState(pid);
        return threadCpuMomentUsage(lastMomentProcessState, fullState);
    }

    private Map<Integer, Double> threadCpuMomentUsage(AtomicReference<FullState> prePSReference, FullState currentFullState) {
        FullState preFullState = prePSReference.get();
        prePSReference.set(currentFullState);
        if (preFullState == null || currentFullState == null) {
            return Collections.emptyMap();
        }
        return threadCpuMomentUsage(
                preFullState.cpuState, currentFullState.cpuState,
                preFullState.threadInfo, currentFullState.threadInfo);
    }

    public FullState getCurrentFullState(int pid) {
        try {
            CpuState cpuState = StatParser.getInstance().parseCpuInfo();
            File processFile = new File("/proc", String.valueOf(pid));
            ProcessState processState = StatParser.getInstance().parseProcessInfo(pid);
            File taskFile = new File(processFile, "task");
            File[] taskFiles = taskFile.listFiles();
            if (!taskFile.exists() || taskFiles == null) {
                return null;
            }
            Map<Integer, ThreadState> threadInfo = getThreadInfo(pid, taskFiles);
            return new FullState(cpuState, processState, threadInfo);
        } catch (IOException e) {
            LOGGER.error("get current process state error");
            return null;
        }
    }

    private Map<Integer, ThreadState> getThreadInfo(int pid, File[] taskFiles) throws IOException {
        Map<Integer, ThreadState> threadInfo = Maps.newHashMapWithExpectedSize(taskFiles.length);
        for (File file : taskFiles) {
            ThreadState singleThreadState = StatParser.getInstance().parseThreadInfo(pid, Integer.parseInt(file.getName()));
            //查询时候可能线程已经死去
            if (singleThreadState == null) {
                continue;
            }
            threadInfo.put(singleThreadState.tid, singleThreadState);
        }
        return threadInfo;
    }

    private Map<Integer, Double> threadCpuMomentUsage(
            CpuState preCpuState, CpuState currentCpuState,
            Map<Integer, ThreadState> preThreadInfo, Map<Integer, ThreadState> currentThreadInfo) {
        long cpuTime = currentCpuState.totalTime() - preCpuState.totalTime();
        return doThreadCpuMomentUsage(preThreadInfo, currentThreadInfo, cpuTime);
    }

    private Map<Integer, Double> doThreadCpuMomentUsage(Map<Integer, ThreadState> preThreadInfo,
                                                        Map<Integer, ThreadState> currentThreadInfo,
                                                        double cpuTime) {
        cpuTime = cpuTime / ProcUtil.getCpuNum();
        Map<Integer, Double> result = Maps.newHashMapWithExpectedSize(currentThreadInfo.size() + 1);
        for (Map.Entry<Integer, ThreadState> entry : currentThreadInfo.entrySet()) {
            ThreadState value = preThreadInfo.get(entry.getKey());
            if (value == null) {
                result.put(entry.getKey(), entry.getValue().totalTime() / cpuTime);
            } else {
                result.put(entry.getKey(), (entry.getValue().totalTime() - value.totalTime()) / cpuTime);
            }
        }
        return result;
    }

    public static ProcessStateCalculator getInstance() {
        return INSTANCE;
    }
}
