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
