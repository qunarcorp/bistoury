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

import java.util.List;

/**
 * @author cai.wen
 * @date 19-1-17
 */
class ProcessState {

    public final int pid;
    public final int ppid;
    public final char state;
    public final String command;
    public final int threadNum;
    public final long userTime;
    public final long systemTime;
    /**
     * Amount of time that this process's waited-for children have been scheduled in user mode
     */
    public final long cUserTime;
    /**
     * Amount of time that this process's waited-for children have been scheduled in kernel mode
     */
    public final long cSystemTime;
    //private long start_time;

    private ProcessState(int pid, String command, char state, int ppid, long userTime, long systemTime, long cUserTime, long cSystemTime, int threadNum) {
        this.pid = pid;
        this.ppid = ppid;
        this.state = state;
        this.command = command;
        this.threadNum = threadNum;
        this.userTime = userTime;
        this.systemTime = systemTime;
        this.cUserTime = cUserTime;
        this.cSystemTime = cSystemTime;
    }

    public long totalTime() {
        return userTime + systemTime + cUserTime + cSystemTime;
    }

    static ProcessState parse(List<String> info) {
        return new ProcessState(
                Integer.valueOf(info.get(0)),
                info.get(1),
                info.get(2).charAt(0),
                Integer.valueOf(info.get(3)),
                Long.valueOf(info.get(13)),
                Long.valueOf(info.get(14)),
                Long.valueOf(info.get(15)),
                Long.valueOf(info.get(16)),
                Integer.valueOf(info.get(19))
        );
    }

    @Override
    public String toString() {
        return "ProcessState{" +
                "pid=" + pid +
                ", ppid=" + ppid +
                ", state=" + state +
                ", command='" + command + '\'' +
                ", threadNum=" + threadNum +
                ", userTime=" + userTime +
                ", systemTime=" + systemTime +
                ", cUserTime=" + cUserTime +
                ", cSystemTime=" + cSystemTime +
                '}';
    }
}
