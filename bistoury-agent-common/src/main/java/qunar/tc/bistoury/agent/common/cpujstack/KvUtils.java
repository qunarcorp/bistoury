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

import com.google.common.base.Strings;

/**
 * @author zhenyu.nie created on 2019 2019/1/9 18:19
 */
public class KvUtils {

    public static final String CPU_JSTACK_PREFIX = "cj-";

    public static String getThreadNumKey(String timestamp) {
        return CPU_JSTACK_PREFIX + timestamp + "-threadNum";
    }

    public static String getThreadMomentCpuTimeKey(String timestamp) {
        return getThreadMomentCpuTimeKey(timestamp, null);
    }

    public static String getThreadMomentCpuTimeKey(String timestamp, String threadId) {
        if (Strings.isNullOrEmpty(threadId)) {
            return CPU_JSTACK_PREFIX + timestamp + "-totalCpuTime";
        } else {
            return CPU_JSTACK_PREFIX + timestamp + "-cputime-" + threadId;
        }
    }

    public static String getThreadMinuteCpuTimeKey(String timestamp) {
        return getThreadMinuteCpuTimeKey(timestamp, null);
    }

    public static String getThreadMinuteCpuTimeKey(String timestamp, String threadId) {
        if (Strings.isNullOrEmpty(threadId)) {
            return CPU_JSTACK_PREFIX + timestamp + "-totalMinuteCpuTime";
        } else {
            return CPU_JSTACK_PREFIX + timestamp + "-minuteCpuTime-" + threadId;
        }
    }

    public static String getJStackResultKey(String timestamp) {
        return CPU_JSTACK_PREFIX + timestamp + "-jstack";
    }

    public static String getThreadInfoKey(String timestamp) {
        return CPU_JSTACK_PREFIX + timestamp + "-threadinfo";
    }

    public static String getCollectSuccessKey(String timestamp) {
        return CPU_JSTACK_PREFIX + timestamp + "-success";
    }
}
