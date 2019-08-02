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

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.instrument.client.location.Location;
import qunar.tc.bistoury.instrument.client.spring.el.*;

import java.util.Map;
import java.util.UUID;

/**
 * @author keli.wang
 * @since 2017/3/15
 * 这个类中的代码不要随意删除，即便IDE提示没有找到使用的地方也不要轻易删除，因为一些程序是通过
 * 字节码的形式调用的，如果想知道哪些地方调用了，通过函数名称进行全局搜索
 */
public final class GlobalDebugContext {

    private static final Logger LOG = BistouryLoggger.getLogger();

    private static final String NORMAL_BREAKPOINT_SUFFIX = "-n";
    private static final String CONDITION_BREAKPOINT_SUFFIX = "-c";

    private static final Map<Location, Breakpoint> breakpoints = Maps.newHashMap();

    private static final ThreadLocal<String> breakpointId = new ThreadLocal<>();

    private static SnapshotReceiver snapshotReceiver;

    private final static SpelExpressionParser parser = new SpelExpressionParser(
            new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, null));


    private GlobalDebugContext() {
    }

    static void initSnapshotReceiver(SnapshotReceiver aSnapshotReceiver) {
        synchronized (breakpoints) {
            snapshotReceiver = aSnapshotReceiver;
        }
    }

    static SnapshotReceiver getSnapshotReceiver() {
        return snapshotReceiver;
    }

    static void destroy() {
        synchronized (breakpoints) {
            breakpoints.clear();
            breakpointId.remove();
        }
    }

    static AddBreakpointResult addBreakpoint(Location location, Expression condition) {
        synchronized (breakpoints) {
            Breakpoint oldBreakpoint = breakpoints.get(location);
            if (oldBreakpoint != null && oldBreakpoint.getCondition() == null && condition == null) {
                return new AddBreakpointResult(oldBreakpoint.getId(), false);
            } else if (oldBreakpoint != null && oldBreakpoint.getCondition() != null && condition != null) {
                String oldConditionStr = oldBreakpoint.getCondition().getExpressionString();
                String newConditionStr = condition.getExpressionString();
                if (objectEquals(oldConditionStr, newConditionStr)) {
                    return new AddBreakpointResult(oldBreakpoint.getId(), false);
                }
            }

            String id;
            if (condition == null) {
                id = UUID.randomUUID().toString() + NORMAL_BREAKPOINT_SUFFIX;
            } else {
                id = UUID.randomUUID().toString() + CONDITION_BREAKPOINT_SUFFIX;
            }
            Breakpoint breakpoint = new Breakpoint(id, location, condition);
            breakpoints.put(location, breakpoint);
            if (oldBreakpoint != null) {
                snapshotReceiver.remove(oldBreakpoint.getId());
            }
            return new AddBreakpointResult(breakpoint.getId(), true);
        }
    }

    private static boolean objectEquals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    static void removeBreakpoint(Location location, String breakpointId) {
        synchronized (breakpoints) {
            Breakpoint breakpoint = breakpoints.get(location);
            if (breakpoint != null && breakpoint.getId().equals(breakpointId)) {
                breakpoints.remove(location);
            }
        }
    }

    private static void removeBreakpoint(Breakpoint breakpoint) {
        synchronized (breakpoints) {
            Breakpoint candidate = breakpoints.get(breakpoint.getLocation());
            if (candidate != null && candidate.getId().equals(breakpoint.getId())) {
                breakpoints.remove(breakpoint.getLocation());
            }
        }
    }

    static boolean hasBreakpointSet(final Location location) {
        synchronized (breakpoints) {
            return breakpoints.containsKey(location);
        }
    }


    static String getBreakpointId() {
        return breakpointId.get();
    }

    static Expression prepareBreakpointCondition(String condition) {
        if (Strings.isNullOrEmpty(condition)) {
            return null;
        }

        try {
            return parser.parseRaw(condition);
        } catch (Exception e) {
            LOG.warn("条件表达式：{}解析出错，请检查表达式, 原因：{}", condition, e.getMessage());
            throw new IllegalArgumentException("register breakpoint fail, illegal breakpoint condition: [" + condition + "], " + e.getMessage(), e);
        }
    }


    //方法不要删除，字节码中使用
    public static boolean hasBreakpointSet(final String source, final int line) {
        Location location = new Location(source, line);
        return hasBreakpointSet(location);
    }


    //方法不要删除，字节码中使用
    public static boolean isHit(final String source, final int line) {
        if (snapshotReceiver == null) return false;

        final Location location = new Location(source, line);
        Breakpoint breakpoint;
        synchronized (breakpoints) {
            breakpoint = breakpoints.get(location);
        }
        if (breakpoint == null) {
            return false;
        }

        //有断点没条件的情况
        if (breakpoint.getCondition() == null) {
            return doBreak(breakpoint);
        }

        STATE state = checkCondition(breakpoint.getCondition());
        switch (state) {
            case HIT:
                return doBreak(breakpoint);
            case FAIL:
                removeBreakpoint(breakpoint);
                snapshotReceiver.endFail(breakpoint.getId());
                return false;
            case MISS:
                return false;
            default:
                throw new IllegalStateException("illegal state: " + state);
        }
    }

    private static boolean doBreak(Breakpoint breakpoint) {
        if (breakpoint.trigger()) {
            removeBreakpoint(breakpoint);
            breakpointId.set(breakpoint.getId());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据上下文判断断点条件是否满足
     *
     * @return 如果断点条件满足，返回true，否则返回false。各种异常情况为false
     */
    private static STATE checkCondition(Expression expression) {
        try {
            BreakpointConditionDTO rootObject = SnapshotCapture.get();
            StandardEvaluationContext context = new StandardEvaluationContext(rootObject);
            return expression.getValue(context, Boolean.class) ? STATE.HIT : STATE.MISS;
        } catch (EvaluationException e) {
            System.err.println("error debug condition evaluate: [" + expression.getExpressionString() + "], " + e.getMessage());
            return STATE.FAIL;
        } catch (ParseException e) {
            System.err.println("error debug condition parse: [" + expression.getExpressionString() + "], " + e.getMessage());
            return STATE.FAIL;
        } catch (Throwable e) {
            System.err.println("error debug condition error: [" + expression.getExpressionString() + "], " + e.getMessage());
            return STATE.FAIL;
        }
    }

    static void unRegisterBreakpoint(String source, int line, String breakpointId) {
        Location location = new Location(source, line);
        removeBreakpoint(location, breakpointId);
    }

    private enum STATE {
        HIT, MISS, FAIL
    }

}
