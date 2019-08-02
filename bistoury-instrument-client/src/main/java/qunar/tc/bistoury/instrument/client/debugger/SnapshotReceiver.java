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

package qunar.tc.bistoury.instrument.client.debugger;

import java.util.Map;

public interface SnapshotReceiver {

    void refreshBreakpointExpireTime(String breakpointId);

    void initBreakPoint(String breakpointId, String source, int line);

    void putLocalVariables(String breakpointId, Map<String, Object> localVariables);

    void putFields(String breakpointId, Map<String, Object> fields);

    void putStaticFields(String breakpointId, Map<String, Object> staticFields);

    void fillStacktrace(String breakpointId, StackTraceElement[] stacktrace);

    void setSource(String breakpointId, String source);

    void setLine(String breakpointId, int line);

    void endReceive(String breakpointId);

    void endFail(String breakpointId);

    void remove(String breakpointId);
}
