package qunar.tc.bistoury.agent.common.kv;

import qunar.tc.bistoury.clientside.common.store.BistouryStore;

import java.util.concurrent.TimeUnit;

/**
 * @author zhenyu.nie created on 2019 2019/1/8 19:16
 */
public class KvDbs {

    private static final int DEFAULT_TTL = (int) TimeUnit.DAYS.toSeconds(3);

    private static final String ROCKS_DB = "rocksdb";

    private static final int DEFAULT_MAX_COMPACTIONS = 3;

    private static final KvDb kvDb;

    static {
        kvDb = new RocksDBStoreImpl(BistouryStore.getStorePath(ROCKS_DB), DEFAULT_TTL, DEFAULT_MAX_COMPACTIONS);
    }

    public static KvDb getKvDb() {
        return kvDb;
    }
}
