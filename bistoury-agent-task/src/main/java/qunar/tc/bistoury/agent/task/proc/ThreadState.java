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
class ThreadState {

    public final int tid;
    public final char state;
    public final long userTime;
    public final long systemTime;

    private ThreadState(int tid, char state, long userTime, long systemTime) {
        this.tid = tid;
        this.state = state;
        this.userTime = userTime;
        this.systemTime = systemTime;
    }

    public long totalTime() {
        return userTime + systemTime;
    }

    static ThreadState parse(List<String> info) {
        return new ThreadState(
                Integer.valueOf(info.get(0)),
                info.get(2).charAt(0),
                Long.valueOf(info.get(13)),
                Long.valueOf(info.get(14)));
    }

    @Override
    public String toString() {
        return "ThreadState{" +
                "tid=" + tid +
                ", state=" + state +
                ", userTime=" + userTime +
                ", systemTime=" + systemTime +
                '}';
    }
}
