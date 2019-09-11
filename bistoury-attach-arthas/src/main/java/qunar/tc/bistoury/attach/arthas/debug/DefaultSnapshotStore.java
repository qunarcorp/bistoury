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

package qunar.tc.bistoury.attach.arthas.debug;

import com.google.common.collect.Maps;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.Snapshot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author zhenyu.nie created on 2018 2018/9/21 17:12
 */
public class DefaultSnapshotStore implements SnapshotStore {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final long EXPIRE_MINUTES = 10;

    private final long expireTime = TimeUnit.MINUTES.toMillis(EXPIRE_MINUTES);

    private final ConcurrentHashMap<String, Snapshot> snapshotCache = new ConcurrentHashMap<>();

    private final RemoveListener removeListener;
    private final Lock lock;

    public DefaultSnapshotStore(Lock lock, RemoveListener removeListener, ScheduledExecutorService cleanExecutor) {
        this.removeListener = removeListener;
        this.lock = lock;

        cleanExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (String breakpointId : snapshotCache.keySet()) {
                    cleanCache(breakpointId);
                }
            }
        }, EXPIRE_MINUTES, 1, TimeUnit.MINUTES);
    }

    private void cleanCache(final String breakpointId) {
        lock.lock();
        try {
            Snapshot snapshot = snapshotCache.get(breakpointId);
            if (snapshot != null && System.currentTimeMillis() >= snapshot.getExpireTime()) {
                snapshotCache.remove(breakpointId);
                removeListener.remove(breakpointId, snapshot);
            }
        } finally {
            lock.unlock();
        }

    }

    private long computeExpireTime() {
        return System.currentTimeMillis() + expireTime;
    }

    @Override
    public Snapshot getSnapshot(String id) {
        return snapshotCache.get(id);
    }

    @Override
    public void remove(String id) {
        snapshotCache.remove(id);
    }

    @Override
    public void refreshBreakpointExpireTime(String breakpointId) {
        Snapshot snapshot = snapshotCache.get(breakpointId);
        if (snapshot != null) {
            snapshot.refreshExpireTime(computeExpireTime());
        }
    }

    @Override
    public void initBreakPoint(String breakpointId, String source, int line) {
        snapshotCache.put(breakpointId, new Snapshot(breakpointId, source, line, computeExpireTime()));
    }

    @Override
    public void putLocalVariables(String breakpointId, Map<String, Object> localVariables) {
        logger.debug("start put local variables, {}", breakpointId);
        Snapshot snapshot = snapshotCache.get(breakpointId);
        if (snapshot == null) {
            logger.debug("end put local variables, {}, breakpoint not exist now", breakpointId);
            return;
        }

        Map<String, String> localVariableRecords = Maps.newHashMapWithExpectedSize(localVariables.size());
        for (Map.Entry<String, Object> entry : localVariables.entrySet()) {
            localVariableRecords.put(entry.getKey(), DebugJsonWriter.write(entry.getValue()));
        }

        snapshot.setLocalVariables(localVariableRecords);
        logger.debug("end put local variables, {}, {}", breakpointId, localVariableRecords);
    }

    @Override
    public void putFields(String breakpointId, Map<String, Object> fields) {
        logger.debug("start put fields, {}", breakpointId);
        Snapshot snapshot = snapshotCache.get(breakpointId);
        if (snapshot == null) {
            logger.debug("end put fields, {}, breakpoint not exist now", breakpointId);
            return;
        }

        Map<String, String> fieldRecords = Maps.newHashMapWithExpectedSize(fields.size());
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            fieldRecords.put(entry.getKey(), DebugJsonWriter.write(entry.getValue()));
        }

        snapshot.setFields(fieldRecords);
        logger.debug("end put fields, {}, {}", breakpointId, fieldRecords);
    }

    @Override
    public void putStaticFields(String breakpointId, Map<String, Object> staticFields) {
        logger.debug("start put static fields, {}", breakpointId);
        Snapshot snapshot = snapshotCache.get(breakpointId);
        if (snapshot == null) {
            logger.debug("end put static fields, {}, breakpoint not exist now", breakpointId);
            return;
        }

        Map<String, String> staticFieldRecords = Maps.newHashMapWithExpectedSize(staticFields.size());
        for (Map.Entry<String, Object> entry : staticFields.entrySet()) {
            staticFieldRecords.put(entry.getKey(), DebugJsonWriter.write(entry.getValue()));
        }

        snapshot.setStaticFields(staticFieldRecords);
        logger.debug("end put static fields, {}, {}", breakpointId, staticFieldRecords);
    }

    @Override
    public void fillStacktrace(String breakpointId, StackTraceElement[] stacktrace) {
        logger.debug("start fill stacktrace, {}", breakpointId);
        Snapshot snapshot = snapshotCache.get(breakpointId);
        if (snapshot == null) {
            logger.debug("end fill stacktrace, {}, breakpoint not exist now", breakpointId);
            return;
        }

        String stacktraceRecord = DebugJsonWriter.write(stacktrace);
        snapshot.setStacktrace(stacktraceRecord);
        logger.debug("end fill stacktrace, {}, {}", breakpointId, stacktraceRecord);
    }

    @Override
    public void setSource(String breakpointId, String source) {

    }

    @Override
    public void setLine(String breakpointId, int line) {

    }

    @Override
    public void endReceive(String breakpointId) {
        logger.debug("start end receive, {}", breakpointId);
        Snapshot snapshot = snapshotCache.get(breakpointId);
        if (snapshot == null) {
            logger.debug("end end receive, {}, breakpoint not exist now", breakpointId);
            return;
        }

        snapshot.markInited();
        logger.debug("end end receive, {}", breakpointId);
    }

    @Override
    public void endFail(String breakpointId) {
        logger.debug("start end fail, {}", breakpointId);
        Snapshot snapshot = snapshotCache.get(breakpointId);
        if (snapshot == null) {
            logger.debug("end end fail, {}, breakpoint not exist now", breakpointId);
            return;
        }
        snapshot.markFail();
        logger.debug("end end fail, {}", breakpointId);
    }
}
