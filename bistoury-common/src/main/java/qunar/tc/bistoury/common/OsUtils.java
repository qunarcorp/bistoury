package qunar.tc.bistoury.common;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;

/**
 * @author cai.wen created on 2019/11/11 17:38
 */
public class OsUtils {

    private static final String OS_NAME = System.getProperty("os.name");

    private OsUtils() {
    }

    public static boolean isLinux() {
        return OS_NAME.toLowerCase().startsWith("linux");
    }

    public static boolean isWindows() {
        return OS_NAME.toLowerCase().startsWith("windows");
    }

    public static boolean isMac() {
        return OS_NAME.toLowerCase().startsWith("mac") || OS_NAME.startsWith("darwin");
    }

    public static boolean isSupportPerf() {
        File paranoidFile = new File("/proc/sys/kernel/perf_event_paranoid");
        if (!paranoidFile.exists()) {
            return false;
        }
        try {
            String value = Files.readFirstLine(paranoidFile, Charsets.UTF_8);
            int paranoid = Integer.parseInt(value);
            if (paranoid >= 2) {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("read /proc/sys/kernel/perf_event_paranoid error.", e);
        }
        return true;
    }
}
