package qunar.tc.kv;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.rocksdb.Options;
import org.rocksdb.RemoveEmptyValueCompactionFilter;
import org.rocksdb.RocksDB;
import org.rocksdb.TtlDB;
import qunar.tc.bistoury.common.CharsetUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: leix.xie
 * @date: 2019/1/14 10:30
 * @describe：
 */
public class KVStoreTest {
    final static byte[] value;

    static {
        RocksDB.loadLibrary();
        StringBuilder sb = new StringBuilder(100000);
        for (int i = 0; i < 100000; i++) {
            sb.append(i);
        }
        value = CharsetUtils.toUTF8Bytes(sb.toString());
        System.out.println(value.length);
    }

    /**
     * 主方法
     */
    public static void main(String[] args) throws Exception {
        final TtlDB ttlDB = getTtlDb();
        List<String> keys = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            final String key = String.valueOf(System.currentTimeMillis());
            keys.add(key);
            ttlDB.put(key.getBytes(Charsets.UTF_8), value);
            Thread.sleep(2000);
            long start = System.currentTimeMillis();
            ttlDB.compactRange();
            System.out.println("compact range: " + (System.currentTimeMillis() - start));
        }
        Thread.sleep(TimeUnit.MINUTES.toMillis(1));
        for (String key : keys) {
            System.out.println(key + " = " + ttlDB.get(key.getBytes(Charsets.UTF_8)));
        }
        Thread.sleep(TimeUnit.MINUTES.toMillis(10));
    }

    private static TtlDB getTtlDb() throws Exception {
        final String path = "/home/test/rocsksdb";
        ensureDirectoryExists(path);
        final Options options = new Options();
        final int ttl = (int) TimeUnit.HOURS.toSeconds(1);
        options.setCreateIfMissing(true);
        options.setMaxBackgroundCompactions(2);
        options.setMaxOpenFiles(2);//RocksDB 会将打开的 SST 文件句柄缓存这，这样下次访问的时候就可以直接使用，而不需要重新在打开。
        options.setWriteBufferSize(4194304);//4M, memtable 的最大 size
        options.setMaxWriteBufferNumber(4);//最大 memtable 的个数
        options.setLevel0FileNumCompactionTrigger(2);//当有4个未进行Compact的文件时，达到触发Compact的条件
        //options.setMaxCompactionBytes(0);
        RemoveEmptyValueCompactionFilter filter = new RemoveEmptyValueCompactionFilter();
        final TtlDB ttlDB = TtlDB.open(options, path, ttl, false);
        return ttlDB;
    }

    private static void ensureDirectoryExists(final String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                throw new RuntimeException("mkdirs error, path: " + path);
            }
        }
    }

}
