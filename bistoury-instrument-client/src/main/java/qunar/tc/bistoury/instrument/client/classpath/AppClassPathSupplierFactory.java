package qunar.tc.bistoury.instrument.client.classpath;

/**
 * @author zhenyu.nie created on 2019 2019/7/18 17:30
 */
public interface AppClassPathSupplierFactory {

    AppClassPathSupplier create(String appLibPath);
}
