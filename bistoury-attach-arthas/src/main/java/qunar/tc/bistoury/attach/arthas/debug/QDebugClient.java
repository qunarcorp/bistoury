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

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClient;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.NamedThreadFactory;
import qunar.tc.bistoury.common.Snapshot;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;
import qunar.tc.bistoury.instrument.client.debugger.Debugger;
import qunar.tc.bistoury.instrument.client.debugger.DefaultDebugger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author zhenyu.nie created on 2018 2018/11/22 20:15
 */
public class QDebugClient implements InstrumentClient {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final Debugger debugger;

    private final SnapshotCache snapshotCache;

    private final ScheduledExecutorService cleanExecutor;

    QDebugClient(InstrumentInfo instrumentInfo) {
        logger.info("start init qdebugg client");
        try {
            final Debugger debugger = new DefaultDebugger();
            RemoveListener removeListener = new RemoveListener() {
                @Override
                public void remove(String breakpointId, final Snapshot snapshot) {
                    debugger.unRegisterBreakpoint(snapshot.getSource(), snapshot.getLine(), snapshot.getId());
                }
            };

            this.cleanExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("snapshot-cache-clean"));
            DefaultSnapshotStore snapshotStore = new DefaultSnapshotStore(instrumentInfo.getLock(), removeListener, cleanExecutor);
            debugger.startup(instrumentInfo, snapshotStore);

            this.debugger = debugger;
            this.snapshotCache = snapshotStore;
            logger.info("success init qdebugg client");
        } catch (Throwable e) {
            destroy();
            logger.error("", "error init qdebug client", e);
            throw new IllegalStateException("qdebug client init error", e);
        }
    }

    public String registerBreakpoint(String source, int line, String breakpointCondition) {
        return debugger.registerBreakpoint(source, line, breakpointCondition);
    }

    public void remoteBreakPoint(String breakpointId) {
        Snapshot snapshot = snapshotCache.getSnapshot(breakpointId);
        if (snapshot != null) {
            debugger.unRegisterBreakpoint(snapshot.getSource(), snapshot.getLine(), snapshot.getId());
            snapshotCache.remove(breakpointId);
        }
    }

    public Snapshot getSnapshot(String id) {
        return snapshotCache.getSnapshot(id);
    }

    @Override
    public void destroy() {
        try {
            logger.info("start destroy qdebugclient");
            if (cleanExecutor != null) {
                cleanExecutor.shutdownNow();
            }
            if (debugger != null) {
                debugger.destroy();
            }
            logger.info("end destroy qdebugclient");
        } catch (Exception e) {
            logger.error("", "destroy qdebugclient error", e);
        }
    }
}
