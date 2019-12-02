package qunar.tc.bistoury.common.profiler.compact;

/**
 * @author cai.wen created on 19-11-29 下午5:18
 */
public class CompactClassHelper {

    private static final Trie compactPrefixPackage = new Trie();

    static {
        addCompactClass("java.");
        addCompactClass("javax.");
        addCompactClass("sun.");
        addCompactClass("org.springframework.");
        addCompactClass("org.jboss.");
        addCompactClass("org.apache.");
        addCompactClass("com.sun.");
        addCompactClass("org.mybatis.");
        addCompactClass("com.mysql.");
        addCompactClass("io.netty.");
        addCompactClass("com.google.");
        addCompactClass("ch.qos.");
        addCompactClass("org.slf4j.");
        addCompactClass("io.termd.core.");
        addCompactClass("com.taobao.arthas.");
        addCompactClass("com.taobao.middleware.");
    }

    public static boolean isCompactClass(String className) {
        return compactPrefixPackage.containsPrefixNode(className);
    }

    private static void addCompactClass(String packageName) {
        compactPrefixPackage.insert(packageName);
        compactPrefixPackage.insert(packageName.replace(".", "/"));
    }
}
