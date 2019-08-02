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

/**
 * @author: leix.xie
 * @date: 2018/11/16 16:45
 * @describe：
 */
public class HostInfo {

    /**
     * 操作系统.
     */
    private String osName;

    /**
     * cpu核数
     */
    private int availableProcessors;
    /**
     * cpu当前负载
     */
    private double systemLoadAverage;

    /**
     * cpu平均负载
     */
    private String cpuLoadAverages;

    /**
     * 可使用内存.
     */
    private long totalMemory;

    /**
     * 剩余内存.
     */
    private long freeMemory;

    /**
     * 最大可使用内存.
     */
    private long maxMemory;

    /**
     * 交换空间的总量
     */
    private long totalSwapSpaceSize;

    /**
     * 可用交换空间量
     */
    private long freeSwapSpaceSize;

    /**
     * 总的物理内存.
     */
    private long totalPhysicalMemorySize;

    /**
     * 剩余的物理内存.
     */
    private long freePhysicalMemorySize;

    /**
     * 已使用的物理内存.
     */
    private long usedMemory;

    /**
     * 线程总数.
     */
    private long totalThread;

    /**
     * cpu使用率.
     */
    private double cpuRatio;

    /**
     * 空闲未使用
     */
    private long freeSpace;

    /**
     * 已经使用
     */
    private long usableSpace;

    /**
     * 总容量
     */
    private long totalSpace;

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public double getSystemLoadAverage() {
        return systemLoadAverage;
    }

    public void setSystemLoadAverage(double systemLoadAverage) {
        this.systemLoadAverage = systemLoadAverage;
    }

    public String getCpuLoadAverages() {
        return cpuLoadAverages;
    }

    public void setCpuLoadAverages(String cpuLoadAverages) {
        this.cpuLoadAverages = cpuLoadAverages;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
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

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public long getTotalThread() {
        return totalThread;
    }

    public void setTotalThread(long totalThread) {
        this.totalThread = totalThread;
    }

    public double getCpuRatio() {
        return cpuRatio;
    }

    public void setCpuRatio(double cpuRatio) {
        this.cpuRatio = cpuRatio;
    }

    public long getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(long freeSpace) {
        this.freeSpace = freeSpace;
    }

    public long getUsableSpace() {
        return usableSpace;
    }

    public void setUsableSpace(long usableSpace) {
        this.usableSpace = usableSpace;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }

    @Override
    public String toString() {
        return "HostInfo{" +
                "osName='" + osName + '\'' +
                ", availableProcessors=" + availableProcessors +
                ", cpuLoadAverages='" + cpuLoadAverages + '\'' +
                ", totalMemory=" + totalMemory +
                ", freeMemory=" + freeMemory +
                ", maxMemory=" + maxMemory +
                ", totalSwapSpaceSize=" + totalSwapSpaceSize +
                ", freeSwapSpaceSize=" + freeSwapSpaceSize +
                ", totalPhysicalMemorySize=" + totalPhysicalMemorySize +
                ", freePhysicalMemorySize=" + freePhysicalMemorySize +
                ", usedMemory=" + usedMemory +
                ", totalThread=" + totalThread +
                ", cpuRatio=" + cpuRatio +
                ", freeSpace=" + freeSpace +
                ", usableSpace=" + usableSpace +
                ", totalSpace=" + totalSpace +
                '}';
    }
}
