package qunar.tc.bistoury.attach.arthas.debug;

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClient;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.Snapshot;
import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;
import qunar.tc.bistoury.instrument.client.debugger.Debugger;
import qunar.tc.bistoury.instrument.client.debugger.DefaultDebugger;

/**
 * @author zhenyu.nie created on 2018 2018/11/22 20:15
 */
public class QDebugClient implements InstrumentClient {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final Debugger debugger;

    private final SnapshotCache snapshotCache;

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
            DefaultSnapshotStore snapshotStore = new DefaultSnapshotStore(instrumentInfo.getLock(), removeListener);
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
            if (debugger != null) {
                debugger.destroy();
            }
            logger.info("end destroy qdebugclient");
        } catch (Exception e) {
            logger.error("", "destroy qdebugclient error", e);
        }
    }
}
