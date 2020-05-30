package qunar.tc.bistoury.agent.common.kv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.common.FileUtil;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author leix.xie
 * @date 2020/5/9 18:06
 * @describe
 */
public class KvDbWrapper implements KvDb {
    private static final Logger LOG = LoggerFactory.getLogger(KvDbWrapper.class);
    private static final int DEFAULT_TTL = (int) TimeUnit.DAYS.toSeconds(3);

    private static final String ROCKS_DB = "rocksdb";
    private static final String SQLITE = "sqlite";

    private static final int DEFAULT_MAX_COMPACTIONS = 3;

    private final KvDb kvdb;

    public KvDbWrapper() {
        final String dbType = System.getProperty("bistoury.store.db", ROCKS_DB);

        if (SQLITE.equalsIgnoreCase(dbType)) {
            final String rocksDbPath = BistouryStore.getStorePath(ROCKS_DB);
            final File file = new File(rocksDbPath);
            if (file.exists()) {
                LOG.info("clean rocksDb data, path:{}", file.getPath());
                FileUtil.deleteDirectory(file, true);
            }

            kvdb = new SQLiteStoreImpl(BistouryStore.getStorePath(SQLITE), DEFAULT_TTL);
        } else {

            kvdb = new RocksDBStoreImpl(BistouryStore.getStorePath(ROCKS_DB), DEFAULT_TTL, DEFAULT_MAX_COMPACTIONS);
        }

    }

    @Override
    public String get(String key) {
        return kvdb.get(key);
    }

    @Override
    public void put(String key, String value) {
        kvdb.put(key, value);
    }

    @Override
    public void putBatch(Map<String, String> data) {
        kvdb.putBatch(data);
    }
}
