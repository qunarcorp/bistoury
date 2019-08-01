package qunar.tc.bistoury.instrument.client.classpath;

import com.google.common.base.Strings;

import java.lang.instrument.Instrumentation;

/**
 * @author zhenyu.nie created on 2019 2019/3/4 16:22
 */
public class DefaultAppLibClassSupplier implements AppLibClassSupplier {

    private final Instrumentation instrumentation;

    public DefaultAppLibClassSupplier(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Override
    public Class<?> get() {
        return findOneAppLibClass(instrumentation);
    }

    private static Class<?> findOneAppLibClass(Instrumentation instrumentation) {
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        final String libClass = System.getProperty("bistoury.app.lib.class");
        if (Strings.isNullOrEmpty(libClass)) {
            System.err.println("can not find lib class");
            throw new IllegalStateException("can not find lib class");
        }
        for (Class clazz : allLoadedClasses) {
            if (libClass.equals(clazz.getName())) {
                return clazz;
            }
        }
        System.err.println("can not find lib class");
        throw new IllegalStateException("can not find lib class");
    }
}
