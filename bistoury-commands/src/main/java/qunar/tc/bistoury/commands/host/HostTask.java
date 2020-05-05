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

package qunar.tc.bistoury.commands.host;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.job.BytesJob;
import qunar.tc.bistoury.agent.common.job.ContinueResponseJob;
import qunar.tc.bistoury.commands.perf.PerfData;
import qunar.tc.bistoury.common.FileUtil;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;
import sun.management.counter.Counter;

import java.io.File;
import java.io.IOException;
import java.lang.management.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: leix.xie
 * @date: 2018/11/15 15:12
 * @describe：
 */
public class HostTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(HostTask.class);

    private static final Joiner SPACE_JOINER = Joiner.on(" ").skipNulls();

    private static final String LOADAVG_FILENAME = "/proc/loadavg";

    private static final short KB = 1024;

    private final String id;

    private final int pid;

    private final ResponseHandler handler;

    private final long maxRunningMs;

    private final SettableFuture<Integer> future = SettableFuture.create();

    public HostTask(String id, int pid, ResponseHandler handler, long maxRunningMs) {
        this.id = id;
        this.pid = pid;
        this.handler = handler;
        this.maxRunningMs = maxRunningMs;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getMaxRunningMs() {
        return maxRunningMs;
    }

    @Override
    public ContinueResponseJob createJob() {
        return new Job();
    }

    @Override
    public ListenableFuture<Integer> getResultFuture() {
        return future;
    }

    private class Job extends BytesJob {

        private Job() {
            super(id, handler, future);
        }

        @Override
        protected byte[] getBytes() throws Exception {
            VirtualMachineUtil.VMConnector connect = VirtualMachineUtil.connect(pid);
            MxBean mxBean = new MxBean(getCounters(pid),
                    connect.getRuntimeMXBean(),
                    connect.getOperatingSystemMXBean(),
                    connect.getMemoryMXBean(),
                    connect.getThreadMXBean(),
                    connect.getClassLoadingMXBean(),
                    connect.getGarbageCollectorMXBeans(),
                    connect.getMemoryPoolMXBeans());

            Map<String, Object> result = new HashMap<>();
            result.put("type", "hostInfo");
            result.put("jvm", getJvmInfo(mxBean));
            result.put("host", getHostInfo(mxBean));
            result.put("memPool", getMemoryPoolMXBeansInfo(mxBean.getMemoryPoolMXBeans()));
            result.put("visuaGC", getVisuaGCInfo(mxBean.getCounters()));
            return JacksonSerializer.serializeToBytes(result);
        }

        @Override
        public ListeningExecutorService getExecutor() {
            return AgentRemotingExecutor.getExecutor();
        }
    }

    private Map<String, Counter> getCounters(int pid) {
        try {
            PerfData prefData = PerfData.connect(pid);
            return prefData.getAllCounters();
        } catch (Exception e) {
            logger.warn("get perf counters error", e);
            return ImmutableMap.of();
        }
    }

    private static List<MemoryPoolInfo> getMemoryPoolMXBeansInfo(List<MemoryPoolMXBean> memoryPoolMXBeans) {
        List<MemoryPoolInfo> memoryPoolInfos = new ArrayList<>();
        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            MemoryUsage usage = memoryPoolMXBean.getUsage();
            MemoryPoolInfo info = new MemoryPoolInfo(memoryPoolMXBean.getName().replaceAll("^PS|^G1|\\s|\\'|-", ""), usage.getInit() / KB, usage.getUsed() / KB, usage.getCommitted() / KB, usage.getMax() / KB);
            memoryPoolInfos.add(info);
        }
        return memoryPoolInfos;
    }

    private VMSnapshotBean getVisuaGCInfo(Map<String, Counter> counters) {
        VMSnapshotBean vmSnapshotBean = new VMSnapshotBean();
        vmSnapshotBean.setEdenSize(getValue(counters, "sun.gc.generation.0.space.0.maxCapacity") / KB);
        vmSnapshotBean.setEdenCapacity(getValue(counters, "sun.gc.generation.0.space.0.capacity") / KB);
        vmSnapshotBean.setEdenUsed(getValue(counters, "sun.gc.generation.0.space.0.used") / KB);
        vmSnapshotBean.setEdenGCEvents(getValue(counters, "sun.gc.collector.0.invocations"));
        vmSnapshotBean.setEdenGCTime(getValue(counters, "sun.gc.collector.0.time"));

        vmSnapshotBean.setSurvivor0Size(getValue(counters, "sun.gc.generation.0.space.1.maxCapacity") / KB);
        vmSnapshotBean.setSurvivor0Capacity(getValue(counters, "sun.gc.generation.0.space.1.capacity") / KB);
        vmSnapshotBean.setSurvivor0Used(getValue(counters, "sun.gc.generation.0.space.1.used") / KB);

        vmSnapshotBean.setSurvivor1Size(getValue(counters, "sun.gc.generation.0.space.2.maxCapacity") / KB);
        vmSnapshotBean.setSurvivor1Capacity(getValue(counters, "sun.gc.generation.0.space.2.capacity") / KB);
        vmSnapshotBean.setSurvivor1Used(getValue(counters, "sun.gc.generation.0.space.2.used") / KB);

        vmSnapshotBean.setTenuredSize(getValue(counters, "sun.gc.generation.1.space.0.maxCapacity") / KB);
        vmSnapshotBean.setTenuredCapacity(getValue(counters, "sun.gc.generation.1.space.0.capacity") / KB);
        vmSnapshotBean.setTenuredUsed(getValue(counters, "sun.gc.generation.1.space.0.used") / KB);
        vmSnapshotBean.setTenuredGCEvents(getValue(counters, "sun.gc.collector.1.invocations"));
        vmSnapshotBean.setTenuredGCTime(getValue(counters, "sun.gc.collector.1.time"));

        vmSnapshotBean.setPermSize(getValue(counters, "sun.gc.generation.2.space.0.maxCapacity") / KB);
        vmSnapshotBean.setPermCapacity(getValue(counters, "sun.gc.generation.2.space.0.capacity") / KB);
        vmSnapshotBean.setPermUsed(getValue(counters, "sun.gc.generation.2.space.0.used") / KB);

        vmSnapshotBean.setMetaSize(getValue(counters, "sun.gc.metaspace.maxCapacity") / KB);
        vmSnapshotBean.setMetaCapacity(getValue(counters, "sun.gc.metaspace.capacity") / KB);
        vmSnapshotBean.setMetaUsed(getValue(counters, "sun.gc.metaspace.used") / KB);

        vmSnapshotBean.setClassLoadTime(getValue(counters, "sun.cls.time"));
        vmSnapshotBean.setClassesLoaded(getValue(counters, "java.cls.loadedClasses"));
        vmSnapshotBean.setClassesUnloaded(getValue(counters, "java.cls.unloadedClasses"));

        vmSnapshotBean.setTotalCompileTime(getValue(counters, "java.ci.totalTime"));
        vmSnapshotBean.setTotalCompile(getValue(counters, "sun.ci.totalCompiles"));

        vmSnapshotBean.setLastGCCause(getStringValue(counters, "sun.gc.lastCause"));

        return vmSnapshotBean;
    }

    private Long getValue(Map<String, Counter> counters, String key) {
        Counter counter = counters.get(key);
        if (counter != null && counter.getValue() != null) {
            return Long.valueOf(counter.getValue().toString());
        }
        return 0L;
    }

    private static String getStringValue(Map<String, Counter> counters, String key) {
        Counter counter = counters.get(key);
        if (counter != null && counter.getValue() != null) {
            return counter.getValue().toString();
        }
        return "";
    }

    private static VMSummaryInfo getJvmInfo(MxBean mxBean) {
        Map<String, Counter> counters = mxBean.getCounters();
        RuntimeMXBean runtimeBean = mxBean.getRuntimeBean();
        OperatingSystemMXBean osBean = mxBean.getOsBean();
        ThreadMXBean threadBean = mxBean.getThreadBean();
        ClassLoadingMXBean classLoadingBean = mxBean.getClassLoadingBean();
        MemoryMXBean memoryMXBean = mxBean.getMemoryMXBean();

        VMSummaryInfo vmInfo = new VMSummaryInfo();

        //运行时间
        vmInfo.setUpTime(runtimeBean.getUptime());
        //进程CPU时间
        vmInfo.setProcessCpuTime(osBean.getProcessCpuTime() / 1000000);
        //操作系统
        vmInfo.setOs(osBean.getName());
        //体系结构
        vmInfo.setOsArch(osBean.getArch());
        //cpu核数
        vmInfo.setAvailableProcessors(osBean.getAvailableProcessors());
        //提交虚拟内存
        vmInfo.setCommitedVirtualMemory(osBean.getCommittedVirtualMemorySize() / KB);
        //总物理内存
        vmInfo.setTotalPhysicalMemorySize(osBean.getTotalPhysicalMemorySize() / KB);
        //空闲物理内存
        vmInfo.setFreePhysicalMemorySize(osBean.getFreePhysicalMemorySize() / KB);
        //总交换空间
        vmInfo.setTotalSwapSpaceSize(osBean.getTotalSwapSpaceSize() / KB);
        //空闲交换空间
        vmInfo.setFreeSwapSpaceSize(osBean.getFreeSwapSpaceSize() / KB);

        //虚拟机、JIT编译器
        vmInfo.setVmName(runtimeBean.getVmName());
        vmInfo.setJitCompiler(runtimeBean.getVmName());
        //供应商
        vmInfo.setVmVendor(runtimeBean.getVmVendor());
        //JDK版本
        vmInfo.setJdkVersion(getStringValue(counters, "java.property.java.version"));
        //vm版本
        vmInfo.setVmVersion(runtimeBean.getVmVersion());
        //活动线程
        vmInfo.setCurrentThreadCount(threadBean.getThreadCount());
        //线程峰值
        vmInfo.setPeakThreadCount(threadBean.getPeakThreadCount());
        //守护线程数
        vmInfo.setDaemonThreadCount(threadBean.getDaemonThreadCount());
        //启动线程总数
        vmInfo.setTotalStartedThreadCount(threadBean.getTotalStartedThreadCount());

        //已加载当前类
        vmInfo.setLoadedClassCount(classLoadingBean.getLoadedClassCount());
        //已加载类总数
        vmInfo.setTotalLoadedClassCount(classLoadingBean.getTotalLoadedClassCount());
        //已卸载类总数
        vmInfo.setUnloadedClassCount(classLoadingBean.getUnloadedClassCount());

        //heap信息
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        //堆提交内存
        vmInfo.setHeapCommitedMemory(memoryUsage.getCommitted() / KB);
        //当前堆内存
        vmInfo.setHeapUsedMemory(memoryUsage.getUsed() / KB);
        //最大堆大小
        vmInfo.setHeapMaxMemory(memoryUsage.getMax() / KB);

        memoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        //非堆提交内存
        vmInfo.setNonHeapCommitedMemory(memoryUsage.getCommitted() / KB);
        //当前非堆内存
        vmInfo.setNonHeapUsedMemory(memoryUsage.getUsed() / KB);
        //最大非堆大小
        vmInfo.setNonHeapMaxMemory(memoryUsage.getMax() / KB);

        //垃圾收集器
        vmInfo.setGcInfos(getGCInfo(mxBean));

        //jvm参数
        vmInfo.setVmOptions(SPACE_JOINER.join(runtimeBean.getInputArguments()));
        //类路径
        vmInfo.setClassPath(runtimeBean.getClassPath());
        //库路径
        vmInfo.setLibraryPath(runtimeBean.getLibraryPath());
        //引导类路径
        try {
            vmInfo.setBootClassPath(runtimeBean.getBootClassPath());
        } catch (Exception e) {
            //jdk9以上版本会抛出 UnsupportedOperationException，忽略
        }

        return vmInfo;
    }

    private static List<String> getGCInfo(MxBean mxBean) {
        List<GarbageCollectorMXBean> gcMxBeans = mxBean.getGcMxBeans();
        List<String> gcInfos = new ArrayList<>(gcMxBeans.size());
        for (GarbageCollectorMXBean b : gcMxBeans) {
            GCBean gcBean = new GCBean();
            gcBean.name = b.getName();
            gcBean.gcCount = b.getCollectionCount();
            gcBean.gcTime = b.getCollectionTime();
            gcInfos.add(gcBean.toString());
        }
        return gcInfos;
    }

    private static HostInfo getHostInfo(MxBean mxBean) {
        OperatingSystemMXBean osBean = mxBean.getOsBean();
        ThreadMXBean threadBean = mxBean.getThreadBean();

        HostInfo hostInfo = new HostInfo();
        String osName = System.getProperty("os.name");
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        String cpuLoadAverages = null;
        if (osName != null && osName.toLowerCase().contains("linux")) {
            try {
                File file = new File(LOADAVG_FILENAME);
                cpuLoadAverages = FileUtil.readFile(file);
            } catch (IOException e) {
                logger.error("get CPU Load Averages error", e);
            }
        }

        double systemLoadAverage = osBean.getSystemLoadAverage();
        // 可使用内存
        long totalMemory = Runtime.getRuntime().totalMemory() / KB;
        // 剩余内存
        long freeMemory = Runtime.getRuntime().freeMemory() / KB;
        // 最大可使用内存
        long maxMemory = Runtime.getRuntime().maxMemory() / KB;

        //总交换空间
        long totalSwapSpaceSize = osBean.getTotalSwapSpaceSize() / KB;
        //空闲交换空间
        long freeSwapSpaceSize = osBean.getFreeSwapSpaceSize() / KB;
        //总物理内存
        long totalPhysicalMemorySize = osBean.getTotalPhysicalMemorySize() / KB;
        //空闲物理内存
        long freePhysicalMemorySize = osBean.getFreePhysicalMemorySize() / KB;
        //系统CPU利用率
        double systemCpuLoad = osBean.getSystemCpuLoad();

        long totalThread = threadBean.getTotalStartedThreadCount();

        //获取磁盘信息
        getDiskInfo(hostInfo);

        hostInfo.setAvailableProcessors(availableProcessors);
        hostInfo.setSystemLoadAverage(systemLoadAverage);
        hostInfo.setCpuLoadAverages(Strings.nullToEmpty(cpuLoadAverages));
        hostInfo.setOsName(osName);
        hostInfo.setTotalMemory(totalMemory);
        hostInfo.setFreeMemory(freeMemory);
        hostInfo.setMaxMemory(maxMemory);
        hostInfo.setTotalSwapSpaceSize(totalSwapSpaceSize);
        hostInfo.setFreeSwapSpaceSize(freeSwapSpaceSize);
        hostInfo.setTotalPhysicalMemorySize(totalPhysicalMemorySize);
        hostInfo.setFreePhysicalMemorySize(freePhysicalMemorySize);
        hostInfo.setUsedMemory(totalPhysicalMemorySize - freePhysicalMemorySize);
        hostInfo.setTotalThread(totalThread);
        hostInfo.setCpuRatio(systemCpuLoad);
        return hostInfo;
    }

    private static void getDiskInfo(HostInfo hostInfo) {
        File[] disks = File.listRoots();
        long freeSpace = 0;
        long usableSpace = 0;
        long totalSpace = 0;
        for (File file : disks) {
            freeSpace += file.getFreeSpace() / KB;
            usableSpace += file.getUsableSpace() / KB;
            totalSpace += file.getTotalSpace() / KB;
        }
        hostInfo.setFreeSpace(freeSpace);
        hostInfo.setUsableSpace(usableSpace);
        hostInfo.setTotalSpace(totalSpace);
    }

    private static class MemoryPoolInfo {
        private String key;
        private String name;
        private long init;
        private long used;
        private long committed;
        private long max;

        public MemoryPoolInfo() {
        }

        public MemoryPoolInfo(String name, long init, long used, long committed, long max) {
            this.key = name;
            this.name = name;
            this.init = init;
            this.used = used;
            this.committed = committed;
            this.max = max;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getInit() {
            return init;
        }

        public void setInit(long init) {
            this.init = init;
        }

        public long getUsed() {
            return used;
        }

        public void setUsed(long used) {
            this.used = used;
        }

        public long getCommitted() {
            return committed;
        }

        public void setCommitted(long committed) {
            this.committed = committed;
        }

        public long getMax() {
            return max;
        }

        public void setMax(long max) {
            this.max = max;
        }
    }

    static class MxBean {
        private final Map<String, Counter> counters;

        private final RuntimeMXBean runtimeBean;

        private final OperatingSystemMXBean osBean;

        private final MemoryMXBean memoryMXBean;

        private final ThreadMXBean threadBean;

        private final ClassLoadingMXBean classLoadingBean;

        private final List<GarbageCollectorMXBean> gcMxBeans;

        private final List<MemoryPoolMXBean> memoryPoolMXBeans;

        MxBean(Map<String, Counter> counters, RuntimeMXBean runtimeBean, OperatingSystemMXBean osBean,
               MemoryMXBean memoryMXBean, ThreadMXBean threadBean, ClassLoadingMXBean classLoadingBean,
               List<GarbageCollectorMXBean> gcMxBeans, List<MemoryPoolMXBean> memoryPoolMXBeans) {
            this.counters = counters;
            this.runtimeBean = runtimeBean;
            this.osBean = osBean;
            this.memoryMXBean = memoryMXBean;
            this.threadBean = threadBean;
            this.classLoadingBean = classLoadingBean;
            this.gcMxBeans = gcMxBeans;
            this.memoryPoolMXBeans = memoryPoolMXBeans;
        }

        public Map<String, Counter> getCounters() {
            return counters;
        }

        public RuntimeMXBean getRuntimeBean() {
            return runtimeBean;
        }

        public OperatingSystemMXBean getOsBean() {
            return osBean;
        }

        public MemoryMXBean getMemoryMXBean() {
            return memoryMXBean;
        }

        public ThreadMXBean getThreadBean() {
            return threadBean;
        }

        public ClassLoadingMXBean getClassLoadingBean() {
            return classLoadingBean;
        }

        public List<GarbageCollectorMXBean> getGcMxBeans() {
            return gcMxBeans;
        }

        public List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
            return memoryPoolMXBeans;
        }
    }
}