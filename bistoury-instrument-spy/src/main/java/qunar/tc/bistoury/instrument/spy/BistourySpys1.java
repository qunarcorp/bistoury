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

package qunar.tc.bistoury.instrument.spy;

import java.lang.reflect.Method;

/**
 * @author zhenyu.nie created on 2018 2018/11/24 19:32
 */
public class BistourySpys1 {

    public static final String HAS_BREAKPOINT_SET = "hasBreakpointSet";
    public static final String IS_HIT = "isHit";
    public static final String PUT_LOCAL_VARIABLE = "putLocalVariable";
    public static final String PUT_FIELD = "putField";
    public static final String PUT_STATIC_FIELD = "putStaticField";
    public static final String FILL_STACKTRACE = "fillStacktrace";
    public static final String DUMP = "dump";
    public static final String END_RECEIVE = "endReceive";
    public static final String START_MONITOR = "start";
    public static final String STOP_MONITOR = "stop";
    public static final String EXCEPTION_MONITOR = "exception";

    private static volatile Method HAS_BREAKPOINT_SET_METHOD;
    private static volatile Method IS_HIT_METHOD;
    private static volatile Method PUT_LOCAL_VARIABLE_METHOD;
    private static volatile Method PUT_FIELD_METHOD;
    private static volatile Method PUT_STATIC_FIELD_METHOD;
    private static volatile Method FILL_STACK_TRACE_METHOD;
    private static volatile Method DUMP_METHOD;
    private static volatile Method END_RECEIVE_METHOD;
    private static volatile Method START_MONITOR_METHOD;
    private static volatile Method STOP_MONITOR_METHOD;
    private static volatile Method EXCEPTION_MONITOR_METHOD;

    public static boolean hasBreakpointSet(String source, int line) {
        final boolean defaultValue = false;
        try {
            return (boolean) doInvokeMethod(HAS_BREAKPOINT_SET_METHOD, defaultValue, new Object[]{source, line});
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            return defaultValue;
        }
    }

    public static boolean isHit(String source, int line) {
        final boolean defaultValue = false;
        try {
            return (boolean) doInvokeMethod(IS_HIT_METHOD, defaultValue, new Object[]{source, line});
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            return defaultValue;
        }
    }

    public static void putLocalVariable(String key, Object value) {
        final Void defaultValue = null;
        try {
            doInvokeMethod(BistourySpys1.PUT_LOCAL_VARIABLE_METHOD, defaultValue, new Object[]{key, value});
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    public static void putField(String key, Object value) {
        final Void defaultValue = null;
        try {
            doInvokeMethod(BistourySpys1.PUT_FIELD_METHOD, defaultValue, new Object[]{key, value});
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    public static void putStaticField(String key, Object value) {
        final Void defaultValue = null;
        try {
            doInvokeMethod(BistourySpys1.PUT_STATIC_FIELD_METHOD, defaultValue, new Object[]{key, value});
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    public static void fillStacktrace(String source, int line, Throwable e) {
        final Void defaultValue = null;
        try {
            doInvokeMethod(BistourySpys1.FILL_STACK_TRACE_METHOD, defaultValue, new Object[]{source, line, e});
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    public static void dump(String source, int line) {
        final Void defaultValue = null;
        try {
            doInvokeMethod(BistourySpys1.DUMP_METHOD, defaultValue, new Object[]{source, line});
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    public static void endReceive(String source, int line) {
        final Void defaultValue = null;
        try {
            doInvokeMethod(BistourySpys1.END_RECEIVE_METHOD, defaultValue, new Object[]{source, line});
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }


    public static Long start() {
        final Long defaultValue = 0L;
        try {
            return (long) doInvokeMethod(START_MONITOR_METHOD, defaultValue, new Object[]{});
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            return defaultValue;
        }
    }

    public static void stop(String key, Long startTime) {
        final Void defaultValue = null;
        try {
            doInvokeMethod(STOP_MONITOR_METHOD, defaultValue, new Object[]{key, startTime});
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    public static void exception(String key) {
        final Void defaultValue = null;
        try {
            doInvokeMethod(EXCEPTION_MONITOR_METHOD, defaultValue, new Object[]{key});
        } catch (Throwable e) {
            e.printStackTrace(System.err);
        }
    }

    private static Object doInvokeMethod(Method method, Object defaultValue, Object[] args) throws Throwable {
        if (method == null) {
            return defaultValue;
        }

        return method.invoke(null, args);
    }

    public static void init(Method hasBreakpointSet,
                            Method isHit,
                            Method putLocalVariable,
                            Method putField,
                            Method putStaticField,
                            Method fillStackTrace,
                            Method dump,
                            Method endReceive,
                            Method startMonitor,
                            Method stopMonitor,
                            Method exceptionMonitor) {
        HAS_BREAKPOINT_SET_METHOD = hasBreakpointSet;
        IS_HIT_METHOD = isHit;
        PUT_LOCAL_VARIABLE_METHOD = putLocalVariable;
        PUT_FIELD_METHOD = putField;
        PUT_STATIC_FIELD_METHOD = putStaticField;
        FILL_STACK_TRACE_METHOD = fillStackTrace;
        DUMP_METHOD = dump;
        END_RECEIVE_METHOD = endReceive;
        START_MONITOR_METHOD = startMonitor;
        STOP_MONITOR_METHOD = stopMonitor;
        EXCEPTION_MONITOR_METHOD = exceptionMonitor;
    }

    public static void destroy() {
        HAS_BREAKPOINT_SET_METHOD = null;
        IS_HIT_METHOD = null;
        PUT_LOCAL_VARIABLE_METHOD = null;
        PUT_FIELD_METHOD = null;
        PUT_STATIC_FIELD_METHOD = null;
        FILL_STACK_TRACE_METHOD = null;
        DUMP_METHOD = null;
        END_RECEIVE_METHOD = null;
        START_MONITOR_METHOD = null;
        STOP_MONITOR_METHOD = null;
        EXCEPTION_MONITOR_METHOD = null;
    }
}
