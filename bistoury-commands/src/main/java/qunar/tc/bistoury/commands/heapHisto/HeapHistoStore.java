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

package qunar.tc.bistoury.commands.heapHisto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.kv.KvDb;
import qunar.tc.bistoury.agent.common.kv.KvDbs;
import qunar.tc.bistoury.common.DateUtil;
import qunar.tc.bistoury.common.JacksonSerializer;

import java.util.Collections;
import java.util.List;

/**
 * @author: leix.xie
 * @date: 2019/4/1 10:45
 * @describe：
 */
public class HeapHistoStore {
    private static final Logger logger = LoggerFactory.getLogger(HeapHistoStore.class);
    private static final String PREFIX = "hh-";
    private static final KvDb KV_DB = KvDbs.getKvDb();
    private static final HeapHistoStore INSTANCE = new HeapHistoStore();

    private HeapHistoStore() {

    }

    public static HeapHistoStore getInstance() {
        return INSTANCE;
    }

    public void store(List<HistogramBean> histogramBeans) {
        try {
            if (!histogramBeans.isEmpty()) {
                String currentMinute = String.valueOf(DateUtil.getMinute());
                KV_DB.put(addPrefix(currentMinute), JacksonSerializer.serialize(histogramBeans));
                logger.debug("store heap histo dump, time: {}, heap histo: {}", currentMinute, histogramBeans);
            }
        } catch (Throwable e) {
            logger.error("store heap histo dump error", e);
        }
    }

    public List<HistogramBean> getHistogramBean(long timestamp) {
        final long minute = DateUtil.transformToMinute(timestamp);
        final String res = KV_DB.get(addPrefix(String.valueOf(minute)));
        if (Strings.isNullOrEmpty(res)) {
            return Collections.emptyList();
        }
        List<HistogramBean> histogramBeans = JacksonSerializer.deSerialize(res, new TypeReference<List<HistogramBean>>() {
        });
        return histogramBeans;
    }

    private String addPrefix(final String key) {
        return PREFIX + key;
    }
}
