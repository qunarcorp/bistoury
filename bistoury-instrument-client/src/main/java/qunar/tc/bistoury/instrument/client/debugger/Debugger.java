package qunar.tc.bistoury.instrument.client.debugger;

import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

public interface Debugger {

    boolean startup(InstrumentInfo instrumentInfo, SnapshotReceiver receiver);

    String registerBreakpoint(final String source, final int line, final String breakpointCondition);

    void unRegisterBreakpoint(final String source, final int line, String breakpointId);

    void destroy();
}
