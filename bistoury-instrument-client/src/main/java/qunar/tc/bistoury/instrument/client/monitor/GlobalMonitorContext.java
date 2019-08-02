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

import com.google.common.collect.Maps;
import qunar.tc.bistoury.instrument.client.location.ResolvedSourceLocation;

import java.util.Map;

/**
 * @author: leix.xie
 * @date: 2019/1/15 20:01
 * @describe：
 */
public class GlobalMonitorContext {
    private static final Map<String, Boolean> monitors = Maps.newHashMap();
    private static final String SEPARATOR = "|";

    public static void addMonitor(final ResolvedSourceLocation location) {
        synchronized (monitors) {
            monitors.put(location.getClassSignature() + SEPARATOR + location.getMethodName() + SEPARATOR + location.getMethodDesc(), true);
        }
    }

    public static boolean check(final ResolvedSourceLocation location) {
        synchronized (monitors) {
            return monitors.containsKey(location.getClassSignature() + SEPARATOR + location.getMethodName() + SEPARATOR + location.getMethodDesc());
        }
    }

    public static void destroy() {
        synchronized (monitors) {
            monitors.clear();
        }
    }
}
