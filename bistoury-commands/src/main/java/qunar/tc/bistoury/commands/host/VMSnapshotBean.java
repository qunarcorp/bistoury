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

import java.io.Serializable;

/**
 * @author: leix.xie
 * @date: 2018/11/21 10:55
 * @describe：
 */
public class VMSnapshotBean implements Serializable {
    private static final long serialVersionUID = 7799538099847658353L;
    private Long edenSize;
    private Long edenCapacity;
    private Long edenUsed;
    private Long edenGCTime;
    private Long edenGCEvents;
    private Long survivor0Size;
    private Long survivor0Capacity;
    private Long survivor0Used;
    private Long survivor1Size;
    private Long survivor1Capacity;
    private Long survivor1Used;
    private Long tenuredSize;
    private Long tenuredCapacity;
    private Long tenuredUsed;
    private Long tenuredGCTime;
    private Long tenuredGCEvents;
    private Long permSize;
    private Long permCapacity;
    private Long permUsed;
    private Long metaSize;
    private Long metaCapacity;
    private Long metaUsed;
    private Long classLoadTime;
    private Long classesLoaded;
    private Long classesUnloaded;
    private Long totalCompileTime;
    private Long totalCompile;
    private String lastGCCause;

    public Long getEdenSize() {
        return edenSize;
    }

    public void setEdenSize(Long edenSize) {
        this.edenSize = edenSize;
    }

    public Long getEdenCapacity() {
        return edenCapacity;
    }

    public void setEdenCapacity(Long edenCapacity) {
        this.edenCapacity = edenCapacity;
    }

    public Long getEdenUsed() {
        return edenUsed;
    }

    public void setEdenUsed(Long edenUsed) {
        this.edenUsed = edenUsed;
    }

    public Long getEdenGCTime() {
        return edenGCTime;
    }

    public void setEdenGCTime(Long edenGCTime) {
        this.edenGCTime = edenGCTime;
    }

    public Long getEdenGCEvents() {
        return edenGCEvents;
    }

    public void setEdenGCEvents(Long edenGCEvents) {
        this.edenGCEvents = edenGCEvents;
    }

    public Long getSurvivor0Size() {
        return survivor0Size;
    }

    public void setSurvivor0Size(Long survivor0Size) {
        this.survivor0Size = survivor0Size;
    }

    public Long getSurvivor0Capacity() {
        return survivor0Capacity;
    }

    public void setSurvivor0Capacity(Long survivor0Capacity) {
        this.survivor0Capacity = survivor0Capacity;
    }

    public Long getSurvivor0Used() {
        return survivor0Used;
    }

    public void setSurvivor0Used(Long survivor0Used) {
        this.survivor0Used = survivor0Used;
    }

    public Long getSurvivor1Size() {
        return survivor1Size;
    }

    public void setSurvivor1Size(Long survivor1Size) {
        this.survivor1Size = survivor1Size;
    }

    public Long getSurvivor1Capacity() {
        return survivor1Capacity;
    }

    public void setSurvivor1Capacity(Long survivor1Capacity) {
        this.survivor1Capacity = survivor1Capacity;
    }

    public Long getSurvivor1Used() {
        return survivor1Used;
    }

    public void setSurvivor1Used(Long survivor1Used) {
        this.survivor1Used = survivor1Used;
    }

    public Long getTenuredSize() {
        return tenuredSize;
    }

    public void setTenuredSize(Long tenuredSize) {
        this.tenuredSize = tenuredSize;
    }

    public Long getTenuredCapacity() {
        return tenuredCapacity;
    }

    public void setTenuredCapacity(Long tenuredCapacity) {
        this.tenuredCapacity = tenuredCapacity;
    }

    public Long getTenuredUsed() {
        return tenuredUsed;
    }

    public void setTenuredUsed(Long tenuredUsed) {
        this.tenuredUsed = tenuredUsed;
    }

    public Long getTenuredGCTime() {
        return tenuredGCTime;
    }

    public void setTenuredGCTime(Long tenuredGCTime) {
        this.tenuredGCTime = tenuredGCTime;
    }

    public Long getTenuredGCEvents() {
        return tenuredGCEvents;
    }

    public void setTenuredGCEvents(Long tenuredGCEvents) {
        this.tenuredGCEvents = tenuredGCEvents;
    }

    public Long getPermSize() {
        return permSize;
    }

    public void setPermSize(Long permSize) {
        this.permSize = permSize;
    }

    public Long getPermCapacity() {
        return permCapacity;
    }

    public void setPermCapacity(Long permCapacity) {
        this.permCapacity = permCapacity;
    }

    public Long getPermUsed() {
        return permUsed;
    }

    public void setPermUsed(Long permUsed) {
        this.permUsed = permUsed;
    }

    public Long getMetaSize() {
        return metaSize;
    }

    public void setMetaSize(Long metaSize) {
        this.metaSize = metaSize;
    }

    public Long getMetaCapacity() {
        return metaCapacity;
    }

    public void setMetaCapacity(Long metaCapacity) {
        this.metaCapacity = metaCapacity;
    }

    public Long getMetaUsed() {
        return metaUsed;
    }

    public void setMetaUsed(Long metaUsed) {
        this.metaUsed = metaUsed;
    }

    public Long getClassLoadTime() {
        return classLoadTime;
    }

    public void setClassLoadTime(Long classLoadTime) {
        this.classLoadTime = classLoadTime;
    }

    public Long getClassesLoaded() {
        return classesLoaded;
    }

    public void setClassesLoaded(Long classesLoaded) {
        this.classesLoaded = classesLoaded;
    }

    public Long getClassesUnloaded() {
        return classesUnloaded;
    }

    public void setClassesUnloaded(Long classesUnloaded) {
        this.classesUnloaded = classesUnloaded;
    }

    public Long getTotalCompileTime() {
        return totalCompileTime;
    }

    public void setTotalCompileTime(Long totalCompileTime) {
        this.totalCompileTime = totalCompileTime;
    }

    public Long getTotalCompile() {
        return totalCompile;
    }

    public void setTotalCompile(Long totalCompile) {
        this.totalCompile = totalCompile;
    }

    public String getLastGCCause() {
        return lastGCCause;
    }

    public void setLastGCCause(String lastGCCause) {
        this.lastGCCause = lastGCCause;
    }
}
