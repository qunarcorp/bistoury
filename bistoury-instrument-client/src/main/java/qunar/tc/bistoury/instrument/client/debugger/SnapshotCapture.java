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

import com.google.common.collect.Maps;

import java.util.Map;

public final class SnapshotCapture {

    private static ThreadLocal<Map<String, Object>> localVariables = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return Maps.newHashMap();
        }
    };

    private static ThreadLocal<Map<String, Object>> fields = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return Maps.newHashMap();
        }
    };

    private static ThreadLocal<Map<String, Object>> staticFields = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return Maps.newHashMap();
        }
    };

    public static void putLocalVariable(String key, Object value) {
        if (value == null) {
            return;
        }
        localVariables.get().put(key, value);
    }

    public static void putField(String key, Object value) {
        if (value == null) {
            return;
        }
        fields.get().put(key, value);
    }

    public static void putStaticField(String key, Object value) {
        if (value == null) {
            return;
        }
        staticFields.get().put(key, value);
    }

    public static void fillStacktrace(final String source, final int line, final Throwable e) {
        String breakpointId = GlobalDebugContext.getBreakpointId();
        if (breakpointId == null || breakpointId.isEmpty()) return;

        SnapshotReceiver snapshotReceiver = GlobalDebugContext.getSnapshotReceiver();
        snapshotReceiver.fillStacktrace(breakpointId, e.getStackTrace());
    }


    static synchronized BreakpointConditionDTO get() {
        BreakpointConditionDTO breakpointConditionDTO = new BreakpointConditionDTO();
        breakpointConditionDTO.setFields(fields.get());
        breakpointConditionDTO.setLocalVariables(localVariables.get());
        breakpointConditionDTO.setStaticFields(staticFields.get());
        return breakpointConditionDTO;
    }

    public static void dump(String source, int line) {
        String breakpointId = GlobalDebugContext.getBreakpointId();
        if (breakpointId == null || breakpointId.isEmpty()) return;

        SnapshotReceiver snapshotReceiver = GlobalDebugContext.getSnapshotReceiver();

        Map<String, Object> localVariables = SnapshotCapture.localVariables.get();
        if (localVariables != null && !localVariables.isEmpty()) {
            snapshotReceiver.putLocalVariables(breakpointId, localVariables);
        }

        Map<String, Object> fields = SnapshotCapture.fields.get();
        if (fields != null && !fields.isEmpty()) {
            snapshotReceiver.putFields(breakpointId, fields);
        }

        Map<String, Object> staticFields = SnapshotCapture.staticFields.get();
        if (staticFields != null && !staticFields.isEmpty()) {
            snapshotReceiver.putStaticFields(breakpointId, staticFields);
        }
    }

    public static void endReceive(final String source, final int line) {
        String breakpointId = GlobalDebugContext.getBreakpointId();
        if (breakpointId == null || breakpointId.isEmpty()) return;

        SnapshotReceiver snapshotReceiver = GlobalDebugContext.getSnapshotReceiver();
        try {
            snapshotReceiver.setSource(breakpointId, source);
            snapshotReceiver.setLine(breakpointId, line);
            snapshotReceiver.endReceive(breakpointId);
        } finally {
            reset();
        }
    }

    private static void reset() {
        localVariables.remove();
        fields.remove();
        staticFields.remove();
    }

}
