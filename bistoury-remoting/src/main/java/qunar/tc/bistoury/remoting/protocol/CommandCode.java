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

package qunar.tc.bistoury.remoting.protocol;

import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leix.xie
 * @date 2019/5/13 11:47
 * @describe
 */
public enum CommandCode {
    REQ_TYPE_REFRESH_AGENT_INFO(1, 30),
    REQ_TYPE_REFRESH_TIP(2, 31),

    REQ_TYPE_COMMAND(101, 1),
    REQ_TYPE_CANCEL(102, 2),
    REQ_TYPE_TAB(103, 3),
    REQ_TYPE_JAVA(104, 4),
    REQ_TYPE_QJTOOLS(105, 6),
    REQ_TYPE_ARTHAS(106, 7),

    REQ_TYPE_DEBUG(201, 8),
    REQ_TYPE_JAR_DEBUG(202, 9),
    REQ_TYPE_DECOMPILER(203, 50),

    REQ_TYPE_HOST_JVM(301, 10),
    REQ_TYPE_HOST_THREAD(302, 11),
    REQ_TYPE_HOST_HEAP_HISTO(303, 12),
    REQ_TYPE_JAR_INFO(304, 13),
    REQ_TYPE_CONFIG(305, 14),
    REQ_TYPE_CPU_JSTACK_TIMES(306, 20),
    REQ_TYPE_CPU_JSTACK_THREADS(307, 21),
    REQ_TYPE_CPU_THREAD_NUM(308, 22),

    REQ_TYPE_MONITOR(401, 40),
    REQ_TYPE_QMONITOR_QUERY(402, 41),

    REQ_TYPE_JOB_PAUSE(601, 601),
    REQ_TYPE_JOB_RESUME(602, 602),

    REQ_TYPE_LIST_DOWNLOAD_FILE(701, 701),
    REQ_TYPE_DOWNLOAD_FILE(702, 702),

    REQ_TYPE_PROFILER_START(501, 51),
    REQ_TYPE_PROFILER_STOP(502, 52),
    REQ_TYPE_PROFILER_STATE_SEARCH(503, 53),
    REQ_TYPE_PROFILER_FILE(504, 54),
    REQ_TYPE_PROFILER_FILE_END(505, 55),

    REQ_TYPE_PROFILER_ALL_FILE_END(506, 56),
    REQ_TYPE_PROFILER_FILE_ERROR(507, 57),
    REQ_TYPE_PROFILER_INFO(508, 58);

    private int code;
    private int oldCode;
    private static final Map<Integer, CommandCode> oldCodeMap = new HashMap<>();
    private static final Map<Integer, CommandCode> codeMap = new HashMap<>();

    static {
        for (CommandCode value : CommandCode.values()) {
            codeMap.put(value.getCode(), value);
            oldCodeMap.put(value.getOldCode(), value);
        }
    }

    CommandCode(int code, int oldCode) {
        this.code = code;
        this.oldCode = oldCode;
    }

    public static Optional<CommandCode> valueOfOldCode(int oldCode) {
        CommandCode commandCode = oldCodeMap.get(oldCode);
        if (commandCode == null) {
            return Optional.absent();
        }
        return Optional.of(commandCode);
    }

    public static Optional<CommandCode> valueOfCode(int code) {
        CommandCode commandCode = codeMap.get(code);
        if (commandCode == null) {
            return Optional.absent();
        }
        return Optional.of(commandCode);
    }

    public int getCode() {
        return code;
    }

    public int getOldCode() {
        return oldCode;
    }
}
