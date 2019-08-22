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

package qunar.tc.bistoury.serverside.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leix.xie
 * @date 2019/7/4 19:57
 * @describe
 */
public class ZKClientCache {
    private static final Logger logger = LoggerFactory.getLogger(ZKClientCache.class);

    private static final Map<String, ZKClient> CACHE = new HashMap<>();

    private static final String LOCAL_ZK_TAG_FILE = "/tmp/bistoury/proxy.conf";

    public synchronized static ZKClient get(String address) {

        logger.info("get zkclient for {}", address);
        ZKClient client = CACHE.get(address);
        if (client == null) {
            client = getZkClient(address);
            CACHE.put(address, client);
        } else {
            client = CACHE.get(address);
        }
        client.incrementReference();
        return client;
    }


    private static ZKClient getZkClient(final String address) {
        if (isLocal()) {
            return new MockZkClientImpl(LOCAL_ZK_TAG_FILE);
        }
        return new ZKClientImpl(address);
    }

    private static boolean isLocal() {
        File file = new File(LOCAL_ZK_TAG_FILE);
        return file.exists() && file.isFile();
    }

}
