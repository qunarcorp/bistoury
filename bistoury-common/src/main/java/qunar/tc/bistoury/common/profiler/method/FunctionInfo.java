package qunar.tc.bistoury.common.profiler.method;

import java.util.Objects;

/**
 * @author cai.wen created on 19-12-2 上午8:55
 */
public class FunctionInfo {

    private final String funcName;

    public FunctionInfo(String funcName) {
        this.funcName = funcName;
    }

    public String getFuncName() {
        return funcName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionInfo that = (FunctionInfo) o;
        return Objects.equals(funcName, that.funcName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(funcName);
    }

    @Override
    public String toString() {
        return "FunctionInfo{" +
                "funcName='" + funcName + '\'' +
                '}';
    }
}
