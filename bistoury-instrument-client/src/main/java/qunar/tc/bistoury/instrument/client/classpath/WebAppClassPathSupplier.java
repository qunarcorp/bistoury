package qunar.tc.bistoury.instrument.client.classpath;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.List;

/**
 * @author zhenyu.nie created on 2019 2019/2/19 14:44
 */
public class WebAppClassPathSupplier implements AppClassPathSupplier {

    private final List<String> classPath;

    public WebAppClassPathSupplier(String appLibPath) {
        this.classPath = findClassPath(appLibPath);
    }

    @Override
    public List<String> get() {
        return classPath;
    }

    private List<String> findClassPath(String appLibPath) {
        File libFile = new File(appLibPath);
        final File webRoot = libFile.getParentFile();
        String sourcePath = new File(webRoot, "classes").getAbsolutePath();
        String libPath = libFile.getAbsolutePath();
        return ImmutableList.of(sourcePath, libPath);
    }
}
