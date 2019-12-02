package qunar.tc.bistoury.proxy.util.profiler;

import qunar.tc.bistoury.common.profiler.method.FunctionInfo;
import qunar.tc.bistoury.common.profiler.method.MethodInfo;

import java.util.Objects;

/**
 * @author cai.wen created on 19-11-24
 */
public class MethodCounter implements Comparable<MethodCounter> {

    private final FunctionInfo functionInfo;

    private long count = 0;

    MethodCounter(FunctionInfo methodInfo) {
        this.functionInfo = methodInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodCounter that = (MethodCounter) o;
        return Objects.equals(functionInfo, that.functionInfo);
    }

    @Override
    public int hashCode() {
        return functionInfo.hashCode();
    }

    public long getCount() {
        return count;
    }

    public void add(long delay) {
        count += delay;
    }

    public FunctionInfo getFunctionInfo() {
        return functionInfo;
    }

    @Override
    public int compareTo(MethodCounter o) {
        return (int) (this.count - o.count);
    }

    @Override
    public String toString() {
        return "MethodCounter{" +
                "functionInfo=" + functionInfo +
                ", count=" + count +
                '}';
    }
}