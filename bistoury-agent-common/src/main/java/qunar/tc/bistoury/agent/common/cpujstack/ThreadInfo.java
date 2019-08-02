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

package qunar.tc.bistoury.agent.common.cpujstack;

import java.util.List;

/**
 * @author zhenyu.nie created on 2019 2019/1/9 14:15
 */
public class ThreadInfo {

    private String id;

    private String name;

    private String state;

    private List<String> lockOn;

    private int minuteCpuTime;

    /**
     * 此项为瞬时cpu占用比,实际应该命名为momentCpuTime字段,
     * 为了和低版本的agent保持兼容,所以沿用之前的字段名字
     */
    private int cpuTime;

    private String stack;

    public ThreadInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getLockOn() {
        return lockOn;
    }

    public void setLockOn(List<String> lockOn) {
        this.lockOn = lockOn;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public int getMinuteCpuTime() {
        return minuteCpuTime;
    }

    public void setMinuteCpuTime(int minuteCpuTime) {
        this.minuteCpuTime = minuteCpuTime;
    }

    public int getCpuTime() {
        return cpuTime;
    }

    public void setCpuTime(int cpuTime) {
        this.cpuTime = cpuTime;
    }
}
