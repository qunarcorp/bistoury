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

package qunar.tc.bistoury.agent.common;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhenyu.nie created on 2019 2019/3/19 16:19
 */
public class JavaProcesses {

    private static final Logger logger = LoggerFactory.getLogger(JavaProcesses.class);

    private static final AtomicLong index = new AtomicLong(0);

    private static boolean shutdown = false;

    private static final Map<Long, Process> processes = Maps.newConcurrentMap();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread("process-shutdown-clear") {
            @Override
            public void run() {
                JavaProcesses.clear();
            }
        });
    }

    public static long register(Process process) {
        long i = index.getAndIncrement();
        logger.debug("register java process: {}", i);
        synchronized (JavaProcesses.class) {
            if (!shutdown) {
                processes.put(i, process);
            } else {
                process.destroy();
                throw new IllegalStateException("system already shutdown");
            }
        }
        return i;
    }

    public static void remove(long index) {
        logger.debug("remove java process: {}", index);
        processes.remove(index);
    }

    public static void clear() {
        synchronized (JavaProcesses.class) {
            shutdown = true;
        }
        int count = 0;
        for (Map.Entry<Long, Process> entry : processes.entrySet()) {
            entry.getValue().destroy();
            logger.debug("clear java process: {}", entry.getKey());
            ++count;
        }
        logger.info("clear java process count: " + count);
    }
}