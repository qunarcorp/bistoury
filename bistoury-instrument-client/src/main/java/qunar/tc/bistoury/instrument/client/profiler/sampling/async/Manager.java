package qunar.tc.bistoury.instrument.client.profiler.sampling.async;

import com.google.common.base.Strings;
import one.profiler.AsyncProfiler;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.OsUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author cai.wen created on 2019/11/11 19:57
 */
public class Manager {

    private static final String profilerLibPath;

    private Manager() {
    }

    static {
        File jarFile = new File(Manager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File rootPath = new File(jarFile.getParentFile().getParentFile(), "bin" + File.separator + "async-profiler");
        String libName = System.getProperty(BistouryConstants.PROFILER_LIB_NAME);
        if (Strings.isNullOrEmpty(libName)) {
            if (OsUtils.isLinux()) {
                libName = "async-profiler-1.6-linux-x64.so";
            }
            if (OsUtils.isMac()) {
                libName = "async-profiler-1.6-macos-x64.so";
            }
        }
        profilerLibPath = new File(rootPath, libName).getAbsolutePath();
    }

    private static volatile AsyncProfiler ASYNC_PROFILER = AsyncProfiler.getInstance(profilerLibPath);

    public static long getSamples() {
        return ASYNC_PROFILER.getSamples();
    }

    public static String execute(String command) throws IOException {
        return ASYNC_PROFILER.execute(command);
    }

    //async-profiler profiler.cpp:658
    private static final String STOP_ERROR_DETAIL_MESSAGE = "Profiler is not active";

    public static void stop() {
        try {
            ASYNC_PROFILER.stop();
        } catch (IllegalStateException e) {
            //ignore "Profiler is not active" error
            if (!e.getMessage().equals(STOP_ERROR_DETAIL_MESSAGE)) {
                throw e;
            }
        }
    }
}
