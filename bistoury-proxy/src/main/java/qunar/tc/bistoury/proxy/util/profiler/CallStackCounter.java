package qunar.tc.bistoury.proxy.util.profiler;

import qunar.tc.bistoury.common.profiler.method.FunctionInfo;

import java.util.List;

/**
 * @author cai.wen created on 19-11-24
 */
public class CallStackCounter {

    private final List<FunctionInfo> functionInfos;

    private final long count;

    public CallStackCounter(List<FunctionInfo> funcInfos, long count) {
        this.functionInfos = funcInfos;
        this.count = count;
    }

    public List<FunctionInfo> getFunctionInfos() {
        return functionInfos;
    }

    public long getCount() {
        return count;
    }
}
