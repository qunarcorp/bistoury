package qunar.tc.bistoury.remoting.protocol;

/**
 * @author zhenyu.nie created on 2019 2019/5/23 20:05
 */
public class CodeTypeMappingStores {

    private static CodeTypeMappingStore instance = new DefaultCodeTypeMappingStore();

    public static CodeTypeMappingStore getInstance() {
        return instance;
    }
}
