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

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen
 * @date 19-1-17
 */
public class ProcUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcUtil.class);

    private static final int HZ = 100;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("mm:ss.SS");

    private static final int CPU_NUM;
    private static final String cpuInfoFilePath = "/proc/cpuinfo";

    static {
        int cpuNum = 0;
        try {
            cpuNum = Files.readLines(new File(cpuInfoFilePath), Charsets.UTF_8, new LineProcessor<Integer>() {
                private int cpuNum = 0;

                @Override
                public boolean processLine(String s) throws IOException {
                    if (s.startsWith("processor")) {
                        cpuNum++;
                    }
                    return true;
                }

                @Override
                public Integer getResult() {
                    return cpuNum;
                }
            });
        } catch (IOException e) {
            LOGGER.error("can not get the number of CPUs/cores ");
        } finally {
            CPU_NUM = cpuNum;
        }
    }

    public static String formatJiffies(long jiffies) {
        long milliSeconds = jiffies * 1000 / HZ;
        return TimeUnit.MILLISECONDS.toHours(milliSeconds) + ":"
                + DATE_TIME_FORMATTER.print(milliSeconds);
    }

    public static <T> Map<String, T> transformHexThreadId(Map<Integer, T> value) {
        if (value == null || value.isEmpty()) {
            return new HashMap<>(0);
        }
        Map<String, T> result = Maps.newHashMapWithExpectedSize(value.size());
        for (Map.Entry<Integer, T> entry : value.entrySet()) {
            String threadId = "0x" + Integer.toHexString(entry.getKey());
            result.put(threadId, entry.getValue());
        }
        return result;
    }

    public static int getCpuNum() {
        return CPU_NUM;
    }
}
