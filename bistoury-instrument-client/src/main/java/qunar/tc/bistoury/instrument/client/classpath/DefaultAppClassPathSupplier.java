package qunar.tc.bistoury.instrument.client.classpath;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author zhenyu.nie created on 2019 2019/2/19 20:03
 */
public class DefaultAppClassPathSupplier implements AppClassPathSupplier {

    private final AppClassPathSupplier supplier;

    public DefaultAppClassPathSupplier(AppLibClassSupplier appLibClassSupplier) {
        Class<?> appLibClass = appLibClassSupplier.get();
        String libJarPath = appLibClass
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
        String appLibPath = new File(libJarPath).getParentFile().getAbsolutePath();

        String appSourcePath = System.getProperty("bistoury.app.classes.path");
        if (!Strings.isNullOrEmpty(appSourcePath)) {
            supplier = new SettableAppClassPathSupplier(ImmutableList.of(appLibPath, appSourcePath));
        } else {
            Iterator<AppClassPathSupplierFactory> factoryIterator = ServiceLoader.load(AppClassPathSupplierFactory.class).iterator();
            if (factoryIterator.hasNext()) {
                supplier = factoryIterator.next().create(appLibPath);
            } else {
                supplier = new WebAppClassPathSupplier(appLibPath);
            }
        }
    }

    @Override
    public List<String> get() {
        return supplier.get();
    }
}
