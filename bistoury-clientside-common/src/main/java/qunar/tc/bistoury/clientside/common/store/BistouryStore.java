package qunar.tc.bistoury.clientside.common.store;

import qunar.tc.bistoury.common.FileUtil;

import java.io.File;

/**
 * @author leix.xie
 * @date 2019-07-22 15:46
 * @describe
 */
public class BistouryStore {

    private static final String DEFAULT_CHILD = "default";
    private static final String STORE_PATH;

    static {
        String path = System.getProperty("bistoury.store.path", null);

        if (path == null) {
            path = System.getProperty("catalina.base");
            if (path == null) {
                path = System.getProperty("java.io.tmpdir");
            }
            path = path + File.separator + "cache";
            System.setProperty("bistoury.store.path", path);
        }
        STORE_PATH = path;
    }

    public static String getStorePath(final String child) {
        return FileUtil.dealPath(STORE_PATH, child);
    }

    public static String getDefaultStorePath() {
        return getStorePath(DEFAULT_CHILD);
    }
}
