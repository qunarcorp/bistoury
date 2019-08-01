package qunar.tc.bistoury.ui.common;

import java.util.UUID;

/**
 * @author leix.xie
 * @date 2019/7/2 14:41
 * @describe
 */
public class UUIDUtil {
    public static String generateUniqueId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String generateUniqueId(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes()).toString().replaceAll("-", "");
    }
}
