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
