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

package qunar.tc.bistoury.commands.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.kv.KvDb;
import qunar.tc.bistoury.agent.common.kv.KvDbs;
import qunar.tc.bistoury.agent.common.util.Response;
import qunar.tc.bistoury.clientside.common.monitor.MetricType;
import qunar.tc.bistoury.clientside.common.monitor.MetricsData;
import qunar.tc.bistoury.clientside.common.monitor.MetricsSnapshot;
import qunar.tc.bistoury.common.DateUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: leix.xie
 * @date: 2019/1/8 19:24
 * @describe：
 */
public class QMonitorStore {
    private static final Logger logger = LoggerFactory.getLogger(QMonitorStore.class);

    private static final QMonitorStore INSTANCE = new QMonitorStore();
    private static final KvDb KV_DB = KvDbs.getKvDb();
    private static final String PREFIX = "qm-";
    private static final String LATEST_TIME = PREFIX + "latest_time";
    private static final String MERTICS_SNAPSHOT_FORMAT = "{\"name\":\"%s\",\"timestamp\":%d,\"metricsData\":[]}";
    private static final String EMPTY_STRING = "";
    private static final String EMPTY_CHAR = "''";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int HOUR = (int) TimeUnit.HOURS.toMinutes(1);

    private static final int MAX_ERROR_COUNT = 10;
    private static final byte COUNT_INDEX = 0;
    private static final byte MIN_1_INDEX = 0;
    private static final byte P98_INDEX = 1;

    private static final long MINUTE = TimeUnit.MINUTES.toMillis(1);

    private QMonitorStore() {

    }

    public static QMonitorStore getInstance() {
        return INSTANCE;
    }

    public void store(MetricsSnapshot snapshot) {
        try {
            String currentMinute = String.valueOf(DateUtil.transformToMinute(snapshot.getTimestamp()));
            if (!isEmpty(snapshot.getMetricsData())) {
                KV_DB.put(LATEST_TIME, currentMinute);
                KV_DB.put(addPrefix(currentMinute), MAPPER.writeValueAsString(snapshot));
            }
        } catch (Throwable e) {
            logger.error("store metrics snapshot error", e);
        }
    }

    public Response reportLatest(final String name, Long queryTime) {
        String latestTime = KV_DB.get(LATEST_TIME);
        if (Strings.isNullOrEmpty(latestTime)) {
            return handlerError("latest", -2, "目前没有监控数据");
        }
        long minute = DateUtil.transformToMinute(queryTime);
        if (!latestTime.equals(String.valueOf(minute))) {
            return handlerError("latest", -2, minute + "处的数据还没准备好");
        }

        String metric = KV_DB.get(addPrefix(latestTime));
        if (Strings.isNullOrEmpty(metric)) {
            return handlerError("latest", -2, "监控数据保存失败，跳过这个点");
        }
        return handlerSuccess("latest", metric);
    }

    public Response reportList(String name, Long startTime, Long endTime) {
        if (EMPTY_CHAR.equals(name) || Strings.isNullOrEmpty(name)) {
            name = EMPTY_STRING;
        }

        if (Math.abs(endTime - startTime) > TimeUnit.DAYS.toMillis(3)) {
            return handlerError("list", -1, "查询时间区间超过三天，请修改查询条件");
        }

        long threeDayAgo = DateUtil.plusDays(System.currentTimeMillis(), -3);
        if (startTime < threeDayAgo) {
            return handlerError("list", -1, "监控数据不能查询三天前的数据，请修改查询条件");
        }
        try {
            String latestTime = KV_DB.get(LATEST_TIME);
            if (Strings.isNullOrEmpty(latestTime)) {
                return handlerError("list", -2, "没有查询到监控数据");
            }
            if (startTime >= endTime) {
                return handlerError("list", -1, "开始时间应当小于结束时间");
            }
            Long latestMinute = Long.parseLong(latestTime);
            startTime = DateUtil.transformToMinute(startTime);
            endTime = DateUtil.transformToMinute(endTime);

            if (latestMinute < endTime) {
                endTime = latestMinute;
            }
            final long interval = computeInterval(startTime, endTime);
            List<String> result = new ArrayList<>();
            int errorCount = 0;
            while (endTime >= startTime) {
                try {
                    result.add(polymerize(startTime, interval, name));
                } catch (Exception e) {
                    logger.error("参数聚合错误", e);
                    if (errorCount++ > MAX_ERROR_COUNT) {
                        throw new RuntimeException("监控结果采样发生错误超过" + MAX_ERROR_COUNT + "次，放弃本次查询");
                    }
                    continue;
                } finally {
                    startTime += interval;
                }
            }
            return handlerSuccess("list", result);
        } catch (Throwable e) {
            logger.error("监控数据数据查询错误。", e);
            return handlerError("list", -1, "监控数据数据查询错误。" + e.getClass() + "，" + e.getMessage());
        }
    }

    private String polymerize(final long start, final long interval, final String name) throws Exception {
        List<MetricsSnapshot> snapshots = new ArrayList<>();
        long newStart = start;
        while (newStart < start + interval) {
            snapshots.add(getMetricsSnapshot(name, newStart));
            newStart += MINUTE;
        }
        if (isEmpty(snapshots)) {
            return String.format(MERTICS_SNAPSHOT_FORMAT, name, start);
        }

        if (snapshots.size() == 1) {
            return MAPPER.writeValueAsString(snapshots.get(0));
        }

        Map<String, PolymerizeData> map = groupByMetricsName(snapshots);
        if (isEmpty(map)) {
            return String.format(MERTICS_SNAPSHOT_FORMAT, name, start);
        }
        final List<MetricsData> metricsDataList = new ArrayList<>();
        MetricsSnapshot snapshot = new MetricsSnapshot(name, start, metricsDataList);
        for (Map.Entry<String, PolymerizeData> entry : map.entrySet()) {
            PolymerizeData polymerizeData = entry.getValue();
            if (polymerizeData.type == MetricType.COUNTER.code()) {
                float[] data = new float[1];
                data[0] = average(polymerizeData.count);
                metricsDataList.add(new MetricsData(polymerizeData.name, polymerizeData.type, data));
            } else {
                float[] data = new float[2];
                data[MIN_1_INDEX] = average(polymerizeData.min_1);
                data[P98_INDEX] = average(polymerizeData.p98);
                metricsDataList.add(new MetricsData(polymerizeData.name, polymerizeData.type, data));
            }
        }
        return MAPPER.writeValueAsString(snapshot);
    }

    private Map<String, PolymerizeData> groupByMetricsName(List<MetricsSnapshot> list) {
        Map<String, PolymerizeData> result = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            MetricsSnapshot metricsSnapshot = list.get(i);
            if (metricsSnapshot == null) {
                continue;
            }
            List<MetricsData> metricsDataList = metricsSnapshot.getMetricsData();
            if (isEmpty(metricsDataList)) {
                continue;
            }
            for (int j = 0; j < metricsDataList.size(); j++) {
                MetricsData metricsData = metricsDataList.get(j);
                if (metricsData != null) {
                    final String key = metricsData.getName() + metricsData.getType();
                    PolymerizeData polymerizeData = result.get(key);
                    if (polymerizeData == null) {
                        polymerizeData = new PolymerizeData(metricsData.getName(), metricsData.getType());
                        result.put(key, polymerizeData);
                    }
                    if (metricsData != null) {
                        handleData(metricsData, polymerizeData);
                    }
                }
            }

        }
        return result;
    }

    private void handleData(MetricsData metricsData, PolymerizeData polymerizeData) {
        if (metricsData.getType() == MetricType.COUNTER.code()) {
            float[] data = metricsData.getData();
            if (data != null && data.length == 1) {
                polymerizeData.count.add(data[COUNT_INDEX]);
            }
        } else if (metricsData.getType() == MetricType.TIMER.code()) {
            float[] data = metricsData.getData();
            if (data != null && data.length == 2) {
                polymerizeData.min_1.add(data[MIN_1_INDEX]);
                polymerizeData.p98.add(data[P98_INDEX]);
            }
        }
    }

    private MetricsSnapshot getMetricsSnapshot(final String name, long key) {
        try {
            String metric = KV_DB.get(addPrefix(key));
            if (!Strings.isNullOrEmpty(metric)) {
                return MAPPER.readValue(metric, MetricsSnapshot.class);
            } else {
                return new MetricsSnapshot(name, key, Collections.EMPTY_LIST);
            }
        } catch (Exception e) {
            throw new RuntimeException("rocks db 数据反序列化失败");
        }

    }

    private float sum(List<Float> data) {
        float sum = 0.0f;
        for (Float datum : data) {
            sum += datum;
        }
        return sum;
    }

    private float average(List<Float> data) {
        return sum(data) / (data.size() * 1.0f);
    }

    private String addPrefix(final String key) {
        return PREFIX + key;
    }

    private String addPrefix(final long key) {
        return PREFIX + key;
    }

    private long computeInterval(final long start, final long end) {
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(end - start);
        if (minutes <= 2 * HOUR) {
            return MINUTE;
        } else if (minutes <= 4 * HOUR) {
            return 2 * MINUTE;
        } else if (minutes <= 8 * HOUR) {
            return 4 * MINUTE;
        } else if (minutes <= 12 * HOUR) {
            return 6 * MINUTE;
        } else if (minutes <= 24 * HOUR) {
            return 10 * MINUTE;
        } else if (minutes <= 48 * HOUR) {
            return 20 * MINUTE;
        } else {
            return 30 * MINUTE;
        }
    }

    private Response handlerError(String type, int status, String errorMsg) {
        return new Response(type, status, errorMsg);
    }

    private Response handlerSuccess(String type, Object data) {
        return new Response(type, 0, data);
    }

    class PolymerizeData {
        String name;
        int type;
        List<Float> count = new ArrayList<>();
        List<Float> min_1 = new ArrayList<>();
        List<Float> p98 = new ArrayList<>();

        public PolymerizeData(String name, int type) {
            this.name = name;
            this.type = type;
        }
    }

    private boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }

    private boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }
}
