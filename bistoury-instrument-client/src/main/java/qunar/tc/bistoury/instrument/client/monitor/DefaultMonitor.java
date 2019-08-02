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

import com.google.common.base.Preconditions;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.Status;
import qunar.tc.bistoury.instrument.client.common.ClassFileBuffer;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;
import qunar.tc.bistoury.instrument.client.location.ClassPathLookup;
import qunar.tc.bistoury.instrument.client.location.FormatMessage;
import qunar.tc.bistoury.instrument.client.location.ResolvedSourceLocation;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.concurrent.locks.Lock;

/**
 * @author: leix.xie
 * @date: 2018/12/28 14:13
 * @describe：
 */
public class DefaultMonitor implements Monitor {
    private static final Logger logger = BistouryLoggger.getLogger();

    private InstrumentInfo instrumentInfo;

    private Instrumentation inst;

    private Lock lock;

    private ClassPathLookup classPathLookup;

    private ClassFileBuffer classFileBuffer;

    private volatile Status status = Status.notStart;

    @Override
    public synchronized boolean startup(InstrumentInfo instrumentInfo) {
        if (status == Status.started) {
            return true;
        }
        if (status != Status.notStart) {
            return false;
        }
        Preconditions.checkNotNull(instrumentInfo, "instrumentation not allowed null");
        this.instrumentInfo = instrumentInfo;
        this.inst = instrumentInfo.getInstrumentation();
        this.lock = instrumentInfo.getLock();
        this.classFileBuffer = instrumentInfo.getClassFileBuffer();

        this.classPathLookup = createClassPathLookup();
        if (classPathLookup == null) {
            status = Status.error;
            return false;
        }
        status = Status.started;
        logger.info("qmonitor started");
        return true;
    }

    @Override
    public synchronized String addMonitor(String source, int line) {
        lock.lock();
        try {
            return doAddMonitor(source, line);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeMonitor(String source, int line, String monitorId) {

    }

    private String doAddMonitor(String source, int line) {
        ensureStarted();
        final String path = new File(source).getPath().replace(File.separatorChar, '/');
        final ResolvedSourceLocation location = classPathLookup.resolveSourceLocation(path, line);
        final FormatMessage errorMsg = location.getErrorMessage();
        if (errorMsg != null) {
            final String message = String.format(errorMsg.getFormat(), errorMsg.getParameters());
            logger.error("add monitor failed. error message: {}", message);
            throw new IllegalStateException("add monitor failed, " + message);
        } else {
            if (GlobalMonitorContext.check(location)) {
                return "success";
            } else {
                GlobalMonitorContext.addMonitor(location);
            }

            try {
                boolean success = instrument(source, location, location.getAdjustedLineNumber());
                if (!success) {
                    logger.error("instrument failed. source: {}, line: {}", source, line);
                    throw new IllegalStateException("register breakpoint fail, instrument fail");
                }
                return "success";
            } catch (Throwable e) {
                logger.error("instrument error. source: {}, line: {}", source, line, e);
                throw new IllegalStateException("add monitor error, " + e.getMessage(), e);
            }
        }

    }

    private boolean instrument(String source, ResolvedSourceLocation location, final int line) throws UnmodifiableClassException, ClassNotFoundException {
        ClassFileTransformer transformer = new MonitorClassFileTransformer(classFileBuffer, source, location, line);
        try {
            Class<?> clazz = instrumentInfo.signatureToClass(location.getClassSignature());
            inst.addTransformer(transformer, true);
            inst.retransformClasses(clazz);
            instrumentInfo.addTransformedClasses(clazz);
            return true;
        } finally {
            inst.removeTransformer(transformer);
        }
    }

    private ClassPathLookup createClassPathLookup() {
        try {
            String[] currentWebClassesPath = instrumentInfo.getClassPath().toArray(new String[0]);
            return new ClassPathLookup(false, currentWebClassesPath);
        } catch (Exception e) {
            logger.error("", "cannot create classPathLookup", e);
        }
        return null;
    }

    private void ensureStarted() {
        Preconditions.checkState(status == Status.started, "qmonitor is not properly initialized");
        Preconditions.checkState(instrumentInfo.isRunning(), "qinstrument is not running");
    }

    @Override
    public synchronized void destroy() {
        logger.info("start destroy qmonitor");
        lock.lock();
        try {
            status = Status.closed;
            GlobalMonitorContext.destroy();
        } finally {
            lock.unlock();
        }
    }
}
