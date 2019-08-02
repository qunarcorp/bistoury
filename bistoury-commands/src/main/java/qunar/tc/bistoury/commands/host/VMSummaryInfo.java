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

import java.util.List;

/**
 * @author: leix.xie
 * @date: 2018/11/19 17:45
 * @describe：
 */
public final class VMSummaryInfo {
    /**
     * 运行时间
     * 单位：毫秒
     */
    private long upTime;

    /**
     * 虚拟机
     */
    private String vmName;

    /**
     * 供应商
     */
    private String vmVendor;
    /**
     * vm版本
     */
    private String vmVersion;

    /**
     * JDK版本
     */
    private String jdkVersion;

    /**
     * 单位：毫秒
     * 进程CPU时间
     */
    private long processCpuTime;

    /**
     * JIT编译器
     */
    private String jitCompiler;

    /**
     * 活动线程
     */
    private int currentThreadCount;

    /**
     * 线程峰值
     */
    private int peakThreadCount;

    /**
     * 启动线程总数
     */
    private long totalStartedThreadCount;

    /**
     * 守护线程数
     */
    private int daemonThreadCount;

    /**
     * 已加载当前类
     */
    private long loadedClassCount;

    /**
     * 已加载类总数
     */
    private long totalLoadedClassCount;

    /**
     * 已卸载类总数
     */
    private long unloadedClassCount;

    /**
     * 当前堆内存
     */
    private long heapUsedMemory;

    /**
     * 最大堆大小
     */
    private long heapMaxMemory;

    /**
     * 堆提交内存
     */
    private long heapCommitedMemory;

    /**
     * 非堆提交内存
     */
    private long nonHeapCommitedMemory;

    /**
     * 当前非堆内存
     */
    private long nonHeapUsedMemory;

    /**
     * 最大非堆大小
     */
    private long nonHeapMaxMemory;

    /**
     * 垃圾收集器
     */
    private List<String> gcInfos;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 体系结构
     */
    private String osArch;

    /**
     * 处理器核数
     */
    private int availableProcessors;

    /**
     * 提交虚拟内存
     */
    private long commitedVirtualMemory;

    /**
     * 总物理内存
     */
    private long totalPhysicalMemorySize;

    /**
     * 空闲物理内存
     */
    private long freePhysicalMemorySize;

    /**
     * 总交换空间
     */
    private long totalSwapSpaceSize;

    /**
     * 空闲交换空间
     */
    private long freeSwapSpaceSize;

    /**
     * JVM参数
     */
    private String vmOptions;

    /**
     * 类路径
     */
    private String classPath;

    /**
     * 库路径
     */
    private String libraryPath;

    /**
     * 引导类路径
     */
    private String bootClassPath;

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public String getVmVendor() {
        return vmVendor;
    }

    public void setVmVendor(String vmVendor) {
        this.vmVendor = vmVendor;
    }

    public String getJdkVersion() {
        return jdkVersion;
    }

    public void setJdkVersion(String jdkVersion) {
        this.jdkVersion = jdkVersion;
    }

    public String getVmVersion() {
        return vmVersion;
    }

    public void setVmVersion(String vmVersion) {
        this.vmVersion = vmVersion;
    }

    public long getProcessCpuTime() {
        return processCpuTime;
    }

    public void setProcessCpuTime(long processCpuTime) {
        this.processCpuTime = processCpuTime;
    }

    public String getJitCompiler() {
        return jitCompiler;
    }

    public void setJitCompiler(String jitCompiler) {
        this.jitCompiler = jitCompiler;
    }

    public int getCurrentThreadCount() {
        return currentThreadCount;
    }

    public void setCurrentThreadCount(int currentThreadCount) {
        this.currentThreadCount = currentThreadCount;
    }

    public int getPeakThreadCount() {
        return peakThreadCount;
    }

    public void setPeakThreadCount(int peakThreadCount) {
        this.peakThreadCount = peakThreadCount;
    }

    public long getTotalStartedThreadCount() {
        return totalStartedThreadCount;
    }

    public void setTotalStartedThreadCount(long totalStartedThreadCount) {
        this.totalStartedThreadCount = totalStartedThreadCount;
    }

    public int getDaemonThreadCount() {
        return daemonThreadCount;
    }

    public void setDaemonThreadCount(int daemonThreadCount) {
        this.daemonThreadCount = daemonThreadCount;
    }

    public long getLoadedClassCount() {
        return loadedClassCount;
    }

    public void setLoadedClassCount(long loadedClassCount) {
        this.loadedClassCount = loadedClassCount;
    }

    public long getTotalLoadedClassCount() {
        return totalLoadedClassCount;
    }

    public void setTotalLoadedClassCount(long totalLoadedClassCount) {
        this.totalLoadedClassCount = totalLoadedClassCount;
    }

    public long getUnloadedClassCount() {
        return unloadedClassCount;
    }

    public void setUnloadedClassCount(long unloadedClassCount) {
        this.unloadedClassCount = unloadedClassCount;
    }

    public long getHeapUsedMemory() {
        return heapUsedMemory;
    }

    public void setHeapUsedMemory(long heapUsedMemory) {
        this.heapUsedMemory = heapUsedMemory;
    }

    public long getHeapMaxMemory() {
        return heapMaxMemory;
    }

    public void setHeapMaxMemory(long heapMaxMemory) {
        this.heapMaxMemory = heapMaxMemory;
    }

    public long getHeapCommitedMemory() {
        return heapCommitedMemory;
    }

    public void setHeapCommitedMemory(long heapCommitedMemory) {
        this.heapCommitedMemory = heapCommitedMemory;
    }

    public long getNonHeapCommitedMemory() {
        return nonHeapCommitedMemory;
    }

    public void setNonHeapCommitedMemory(long nonHeapCommitedMemory) {
        this.nonHeapCommitedMemory = nonHeapCommitedMemory;
    }

    public long getNonHeapUsedMemory() {
        return nonHeapUsedMemory;
    }

    public void setNonHeapUsedMemory(long nonHeapUsedMemory) {
        this.nonHeapUsedMemory = nonHeapUsedMemory;
    }

    public long getNonHeapMaxMemory() {
        return nonHeapMaxMemory;
    }

    public void setNonHeapMaxMemory(long nonHeapMaxMemory) {
        this.nonHeapMaxMemory = nonHeapMaxMemory;
    }

    public List<String> getGcInfos() {
        return gcInfos;
    }

    public void setGcInfos(List<String> gcInfos) {
        this.gcInfos = gcInfos;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public long getCommitedVirtualMemory() {
        return commitedVirtualMemory;
    }

    public void setCommitedVirtualMemory(long commitedVirtualMemory) {
        this.commitedVirtualMemory = commitedVirtualMemory;
    }

    public long getTotalPhysicalMemorySize() {
        return totalPhysicalMemorySize;
    }

    public void setTotalPhysicalMemorySize(long totalPhysicalMemorySize) {
        this.totalPhysicalMemorySize = totalPhysicalMemorySize;
    }

    public long getFreePhysicalMemorySize() {
        return freePhysicalMemorySize;
    }

    public void setFreePhysicalMemorySize(long freePhysicalMemorySize) {
        this.freePhysicalMemorySize = freePhysicalMemorySize;
    }

    public long getTotalSwapSpaceSize() {
        return totalSwapSpaceSize;
    }

    public void setTotalSwapSpaceSize(long totalSwapSpaceSize) {
        this.totalSwapSpaceSize = totalSwapSpaceSize;
    }

    public long getFreeSwapSpaceSize() {
        return freeSwapSpaceSize;
    }

    public void setFreeSwapSpaceSize(long freeSwapSpaceSize) {
        this.freeSwapSpaceSize = freeSwapSpaceSize;
    }

    public String getVmOptions() {
        return vmOptions;
    }

    public void setVmOptions(String vmOptions) {
        this.vmOptions = vmOptions;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getLibraryPath() {
        return libraryPath;
    }

    public void setLibraryPath(String libraryPath) {
        this.libraryPath = libraryPath;
    }

    public String getBootClassPath() {
        return bootClassPath;
    }

    public void setBootClassPath(String bootClassPath) {
        this.bootClassPath = bootClassPath;
    }
}

class GCBean {
    String name;
    long gcCount;
    /**
     * 单位：毫秒
     */
    long gcTime;

    @Override
    public String toString() {
        return "名称：" + name + "，收集：" + gcCount + "，总花费时间：" + gcTime + "ms，平均时间：" + (gcCount != 0 ? (gcTime / gcCount) : 0) + "ms  ";
    }
}