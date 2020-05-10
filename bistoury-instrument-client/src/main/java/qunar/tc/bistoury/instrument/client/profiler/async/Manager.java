package qunar.tc.bistoury.instrument.client.profiler.async;

import com.taobao.middleware.logger.Logger;
import one.profiler.AsyncProfiler;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.OsUtils;

import java.io.File;

/**
 * @author cai.wen created on 2019/11/11 19:57
 */
public class Manager {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final String profilerLibPath;

    private static volatile AsyncProfiler ASYNC_PROFILER;

    private Manager() {
    }

    static {
        File jarFile = new File(Manager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File rootPath = new File(jarFile.getParentFile().getParentFile(), "bin" + File.separator + "async-profiler");
        String libName = "";
        if (OsUtils.isLinux()) {
            libName = "async-profiler-1.6-linux-x64.so";
        }
        if (OsUtils.isMac()) {
            libName = "async-profiler-1.6-macos-x64.so";
        }
        profilerLibPath = new File(rootPath, libName).getAbsolutePath();
        logger.info("", "async profiler lib path: {}", profilerLibPath);
        try {
            ASYNC_PROFILER = AsyncProfiler.getInstance(profilerLibPath);
        } catch (Throwable e) {
            logger.error("", "load async profiler lib error.", e);
        }
    }

    public static long getSamples() {
        return ASYNC_PROFILER.getSamples();
    }

    public static String execute(String command) {
        try {
            return ASYNC_PROFILER.execute(command);
        } catch (Throwable e) {
            throw new RuntimeException("execute async command error. command: " + command, e);
        }
    }

    static void tryStop(String command) {
        try {
            ASYNC_PROFILER.execute(command);
        } catch (Throwable e) {
            throw new RuntimeException("execute async command error. command: " + command, e);
        }
    }

    //async-profiler profiler.cpp:658
    private static final String STOP_ERROR_DETAIL_MESSAGE = "Profiler is not active";

    public static void clear() {
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
