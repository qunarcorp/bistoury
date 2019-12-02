package qunar.tc.bistoury.common.profiler.method;

import java.util.Objects;

/**
 * @author cai.wen created on 2019/10/17 10:50
 */
public class MethodInfo {

    private final String className;

    private final String methodName;

    public MethodInfo(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInfo that = (MethodInfo) o;
        return Objects.equals(className, that.className) &&
                Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, methodName);
    }

    @Override
    public String toString() {
        return className + ":" + methodName;
    }
}
