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

package qunar.tc.bistoury.instrument.client.monitor;

import com.google.common.base.Strings;
import qunar.tc.bistoury.instrument.client.metrics.Metrics;

import java.util.concurrent.TimeUnit;

/**
 * @author: leix.xie
 * @date: 2018/12/27 14:47
 * @describe：
 */
public class AgentMonitor {
    public static Long start() {
        long startTime = System.currentTimeMillis();
        return startTime;
    }

    public static void stop(String key, long startTime) {
        if (startTime != 0L && !Strings.isNullOrEmpty(key)) {
            Metrics.timer(key + "_timer").get().update(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
            Metrics.counter(key + "_counter").delta().get().inc();
        }
    }

    public static void exception(String key) {
        Metrics.counter(key + "_exception").delta().get().inc();
    }
}
