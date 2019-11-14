package qunar.tc.bistoury.common;

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
}
