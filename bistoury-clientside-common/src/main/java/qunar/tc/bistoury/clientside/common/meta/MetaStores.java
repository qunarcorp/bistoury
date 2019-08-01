package qunar.tc.bistoury.clientside.common.meta;

/**
 * @author zhenyu.nie created on 2019 2019/1/10 15:40
 */
public class MetaStores {

    private static final MetaStore metaStore = new DefaultMetaStore();

    public static MetaStore getMetaStore() {
        return metaStore;
    }
}
