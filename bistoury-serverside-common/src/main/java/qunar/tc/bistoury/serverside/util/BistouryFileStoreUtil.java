package qunar.tc.bistoury.serverside.util;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.io.File;
import java.io.IOException;

/**
 * @author leix.xie
 * @date 2019/7/4 10:59
 * @describe
 */
public class BistouryFileStoreUtil {
    private static Supplier<String> store = Suppliers.memoize(() -> {
        String path = System.getProperty("bistoury.cache", null);

        if (path == null) {
            path = System.getProperty("catalina.base");
            if (path == null) path = System.getProperty("java.io.tmpdir");
            path = path + File.separator + "cache";
            System.setProperty("bistoury.cache", path);
        }

        File file = new File(path);
        file.mkdirs();

        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    });

    public static String getBistouryStore() {
        return store.get();
    }
}
