package qunar.tc.bistoury.common.profiler.compact;

import java.util.List;

/**
 * @author cai.wen created on 19-11-29 下午5:18
 */
public class CompactClassHelper {

    private CompactClassHelper() {
    }

    private static volatile Trie compactPrefixPackage = new Trie();

    static {
        addCompactClass("java.", compactPrefixPackage);
        addCompactClass("javax.", compactPrefixPackage);
        addCompactClass("sun.", compactPrefixPackage);
        addCompactClass("org.springframework.", compactPrefixPackage);
        addCompactClass("org.jboss.", compactPrefixPackage);
        addCompactClass("org.apache.", compactPrefixPackage);
        addCompactClass("com.sun.", compactPrefixPackage);
        addCompactClass("org.mybatis.", compactPrefixPackage);
        addCompactClass("com.mysql.", compactPrefixPackage);
        addCompactClass("io.netty.", compactPrefixPackage);
        addCompactClass("com.google.", compactPrefixPackage);
        addCompactClass("ch.qos.", compactPrefixPackage);
        addCompactClass("org.slf4j.", compactPrefixPackage);
        addCompactClass("io.termd.core.", compactPrefixPackage);
        addCompactClass("com.taobao.arthas.", compactPrefixPackage);
        addCompactClass("com.taobao.middleware.", compactPrefixPackage);
    }

    public static void init(List<String> compactPrefixPackages) {
        Trie newCompactPrefixPackage = new Trie();
        for (String prefixClass : compactPrefixPackages) {
            addCompactClass(prefixClass, newCompactPrefixPackage);
        }
        compactPrefixPackage = newCompactPrefixPackage;
    }

    public static boolean isCompactClass(String className) {
        return compactPrefixPackage.containsPrefixNode(className);
    }

    private static void addCompactClass(String packageName, Trie trie) {
        trie.insert(packageName);
        trie.insert(packageName.replace(".", "/"));
    }
}
