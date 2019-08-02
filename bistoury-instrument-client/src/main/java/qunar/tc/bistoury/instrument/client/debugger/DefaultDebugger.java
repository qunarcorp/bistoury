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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.Status;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;
import qunar.tc.bistoury.instrument.client.location.ClassPathLookup;
import qunar.tc.bistoury.instrument.client.location.FormatMessage;
import qunar.tc.bistoury.instrument.client.location.Location;
import qunar.tc.bistoury.instrument.client.location.ResolvedSourceLocation;
import qunar.tc.bistoury.instrument.client.spring.el.Expression;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * @author zhenyu.nie created on 2018 2018/11/22 15:26
 */
public class DefaultDebugger implements Debugger {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final Set<Location> instrumented = Sets.newHashSet();

    private InstrumentInfo instrumentInfo;

    private Instrumentation inst;

    private Lock lock;

    private SnapshotReceiver receiver;

    private ClassPathLookup classPathLookup;

    private volatile Status status = Status.notStart;

    @Override
    public synchronized boolean startup(InstrumentInfo instrumentInfo, SnapshotReceiver receiver) {
        if (status == Status.started) {
            return true;
        }

        if (status != Status.notStart) {
            return false;
        }

        Preconditions.checkNotNull(instrumentInfo, "instrument info not allowed null");
        Preconditions.checkNotNull(receiver, "receiver not allowed null");

        this.instrumentInfo = instrumentInfo;
        this.inst = instrumentInfo.getInstrumentation();
        this.lock = instrumentInfo.getLock();
        this.receiver = receiver;

        GlobalDebugContext.initSnapshotReceiver(receiver);

        classPathLookup = createClassPathLookup();
        if (classPathLookup == null) {
            status = Status.error;
            return false;
        }

        status = Status.started;
        logger.info("qdebugger started");
        return true;
    }

    @Override
    public synchronized String registerBreakpoint(String source, int line, String breakpointCondition) {
        lock.lock();
        try {
            return doRegisterBreakpoint(source, line, breakpointCondition);
        } finally {
            lock.unlock();
        }
    }


    private ClassPathLookup createClassPathLookup() {
        try {
            String[] currentWebClassesPath = instrumentInfo.getClassPath().toArray(new String[0]);
            return new ClassPathLookup(false, currentWebClassesPath);
        } catch (Exception e) {
            logger.warn("cannot create classPathLookup", e);
        }
        return null;
    }

    @Override
    public synchronized void unRegisterBreakpoint(String source, int line, String breakpointId) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(source), "source is empty");
        Preconditions.checkArgument(line > 0, "line num should be positive");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(breakpointId), "breakpoint id is empty");
        lock.lock();
        try {
            GlobalDebugContext.unRegisterBreakpoint(source, line, breakpointId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized void destroy() {
        logger.info("start destroy qdebugger");
        lock.lock();
        try {
            status = Status.closed;
            GlobalDebugContext.destroy();
        } finally {
            lock.unlock();
        }
    }

    private void ensureStarted() {
        Preconditions.checkState(status == Status.started, "qdebugger is not properly initialized");
        Preconditions.checkState(instrumentInfo.isRunning(), "qinstrument not running");
    }

    private String doRegisterBreakpoint(String source, int line, String breakpointCondition) {
        ensureStarted();

        final String path = new File(source).getPath().replace(File.separatorChar, '/');
        final ResolvedSourceLocation location = classPathLookup.resolveSourceLocation(path, line);
        final FormatMessage error = location.getErrorMessage();
        if (error != null) {
            final String message = String.format(error.getFormat(), (Object[]) error.getParameters());
            logger.warn("register breakpoint failed. error message: {}", message);
            throw new IllegalStateException("register breakpoint fail, " + message);
        } else {
            final int adjustedLineNumber = location.getAdjustedLineNumber();
            final Location realLocation = new Location(source, adjustedLineNumber);

            Expression expression = GlobalDebugContext.prepareBreakpointCondition(breakpointCondition);
            AddBreakpointResult addBreakpointResult = GlobalDebugContext.addBreakpoint(realLocation, expression);
            String id = addBreakpointResult.getId();
            if (!addBreakpointResult.isNewId()) {
                receiver.refreshBreakpointExpireTime(id);
                return id;
            }

            try {
                boolean success = instrument(source, realLocation, location);
                if (success) {
                    receiver.initBreakPoint(id, realLocation.getSource(), realLocation.getLine());
                    return id;
                } else {
                    logger.warn("instrument failed. source: {}, line: {}", source, line);
                    GlobalDebugContext.removeBreakpoint(realLocation, id);
                    throw new IllegalStateException("register breakpoint fail, instrument fail");
                }
            } catch (Throwable e) {
                logger.debug("doRegisterBreakpoint error：{}", e.getMessage(), e);
                GlobalDebugContext.removeBreakpoint(realLocation, id);
                logger.warn("instrument error. source: {}, line: {}", source, line, e);
                throw new IllegalStateException("register breakpoint error, " + e.getMessage(), e);
            }
        }
    }

    private boolean instrument(String source, Location realLocation, ResolvedSourceLocation location) throws UnmodifiableClassException, ClassNotFoundException {
        if (instrumented.contains(realLocation)) {
            return true;
        }

        ClassFileTransformer transformer = new DebuggerClassFileTransformer(instrumentInfo.getClassFileBuffer(), source, location);
        try {
            Class<?> clazz = instrumentInfo.signatureToClass(location.getClassSignature());
            inst.addTransformer(transformer, true);
            inst.retransformClasses(clazz);
            instrumented.add(realLocation);
            instrumentInfo.addTransformedClasses(clazz);
            return true;
        } finally {
            inst.removeTransformer(transformer);
        }
    }
}
