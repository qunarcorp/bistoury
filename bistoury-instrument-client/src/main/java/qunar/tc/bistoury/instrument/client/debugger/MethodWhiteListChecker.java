package qunar.tc.bistoury.instrument.client.debugger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhaohui.yu
 * 8/10/17
 */
public class MethodWhiteListChecker {
    private static Set<String> WHITE_LIST = new HashSet<>();

    static {
        WHITE_LIST.add("equals");
        WHITE_LIST.add("length");
        WHITE_LIST.add("valueOf");
        WHITE_LIST.add("toString");
        WHITE_LIST.add("hashCode");
        WHITE_LIST.add("compareTo");
        WHITE_LIST.add("size");
        WHITE_LIST.add("count");
    }

    public static void check(String methodName) {
        if (!WHITE_LIST.contains(methodName)) {
            throw new RuntimeException("不允许执行自定义方法");
        }
    }
}
