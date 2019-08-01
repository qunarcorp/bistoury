package qunar.tc.bistoury.instrument.client.classpath;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author zhenyu.nie created on 2019 2019/7/18 14:03
 */
public class SettableAppClassPathSupplier implements AppClassPathSupplier {

    private final List<String> classPath;

    public SettableAppClassPathSupplier(List<String> classPath) {
        this.classPath = ImmutableList.copyOf(classPath);
    }

    @Override
    public List<String> get() {
        return classPath;
    }
}
