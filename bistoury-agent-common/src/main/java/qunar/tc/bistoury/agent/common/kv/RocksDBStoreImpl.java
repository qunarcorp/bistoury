/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.agent.common.kv;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.TtlDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.CharsetUtils;

import java.io.File;
import java.util.Map;


/**
 * @author: leix.xie
 * @date: 2019/1/7 11:19
 * @describe：
 */
public class RocksDBStoreImpl implements KvDb {

    private static final Logger LOG = LoggerFactory.getLogger(RocksDBStoreImpl.class);

    private static final int MB_BYTE = 1048576;

    static {
        RocksDB.loadLibrary();
    }

    private final TtlDB rocksDB;

    RocksDBStoreImpl(String path, int ttl, int maxCompactions) {
        try {
            ensureDirectoryExists(path);

            final Options options = new Options();
            options.setCreateIfMissing(true);
            options.setMaxBackgroundCompactions(maxCompactions);
            options.setMaxOpenFiles(2);//RocksDB 会将打开的 SST 文件句柄缓存这，这样下次访问的时候就可以直接使用，而不需要重新在打开。
            options.setWriteBufferSize(4 * MB_BYTE);//4M, memtable 的最大 size
            options.setMaxWriteBufferNumber(4);//最大 memtable 的个数
            options.setLevel0FileNumCompactionTrigger(4);//当有4个未进行Compact的文件时，达到触发Compact的条件

            this.rocksDB = TtlDB.open(options, path, ttl, false);
            LOG.info("open rocks db success, path:{}, ttl:{}", path, ttl);

        } catch (Exception e) {
            LOG.error("open rocks db error, path:{}, ttl:{}", path, ttl, e);
            throw new RuntimeException(e);
        }
    }

    private void ensureDirectoryExists(final String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                throw new RuntimeException("mkdirs error, path: " + path);
            }
        }
    }

    @Override
    public void put(String key, String value) {
        try {
            final byte[] keyBytes = CharsetUtils.toUTF8Bytes(key);
            final byte[] valueBytes = CharsetUtils.toUTF8Bytes(value);
            if (keyBytes == null || keyBytes.length == 0 || valueBytes == null || value.length() == 0) {
                return;
            }
            rocksDB.put(keyBytes, valueBytes);
        } catch (Exception e) {
            LOG.error("put rocks db error, key:{}, value:{}", key, value, e);
        }
    }

    @Override
    public String get(String key) {
        try {
            final byte[] keyBytes = CharsetUtils.toUTF8Bytes(key);
            if (keyBytes == null || keyBytes.length == 0) {
                return null;
            }
            final byte[] valueBytes = rocksDB.get(keyBytes);
            final String value = CharsetUtils.toUTF8String(valueBytes);
            if (value.length() == 0) {
                return null;
            }
            return value;
        } catch (Exception e) {
            LOG.error("get value from rocks db error, key:{}", key, e);
            return null;
        }
    }

    @Override
    public void putBatch(Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
}
