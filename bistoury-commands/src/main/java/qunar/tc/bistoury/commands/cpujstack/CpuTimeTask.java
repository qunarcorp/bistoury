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

package qunar.tc.bistoury.commands.cpujstack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import joptsimple.internal.Strings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.cpujstack.KvUtils;
import qunar.tc.bistoury.agent.common.kv.KvDb;
import qunar.tc.bistoury.agent.common.util.DateUtils;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author zhenyu.nie created on 2019 2019/1/9 17:39
 */
public class CpuTimeTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(CpuTimeTask.class);

    private static final ListeningExecutorService agentExecutor = AgentRemotingExecutor.getExecutor();

    private volatile ListenableFuture<Integer> future;

    private final String id;

    private final long maxRunningMs;

    private final KvDb kvDb;

    private final String threadId;

    private final DateTime start;

    private final DateTime end;

    private final ResponseHandler handler;

    public CpuTimeTask(String id, long maxRunningMs, KvDb kvDb, String threadId, DateTime start, DateTime end, ResponseHandler handler) {
        this.id = id;
        this.maxRunningMs = maxRunningMs;
        this.kvDb = kvDb;
        this.threadId = threadId;
        this.start = start;
        this.end = end;
        this.handler = handler;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getMaxRunningMs() {
        return maxRunningMs;
    }

    @Override
    public ListenableFuture<Integer> execute() {
        this.future = agentExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return doTask();
            }
        });
        return future;
    }

    private Integer doTask() {
        List<CpuTime> cpuTimes = Lists.newArrayList();

        DateTime time = start;
        while (!time.isAfter(end)) {
            String timestamp = DateUtils.TIME_FORMATTER.print(time);
            int cpuTime = getCpuTime(timestamp);
            if (cpuTime > 0) {
                cpuTimes.add(new CpuTime(timestamp, cpuTime));
            }
            time = time.plusMinutes(1);
        }

        Map<String, Object> map = Maps.newHashMap();

        map.put("type", "cpuTime");
        if (!Strings.isNullOrEmpty(threadId)) {
            map.put("threadId", threadId);
        }
        map.put("cpuTimes", cpuTimes);
        map.put("start", DateUtils.TIME_FORMATTER.print(start));
        map.put("end", DateUtils.TIME_FORMATTER.print(end));
        handler.handle(JacksonSerializer.serializeToBytes(map));
        return 0;
    }

    private int getCpuTime(String timestamp) {
        String key = KvUtils.getThreadMinuteCpuTimeKey(timestamp, threadId);
        String value = kvDb.get(key);
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    @Override
    public void cancel() {
        try {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
        } catch (Exception e) {
            logger.error("cancel cpu time task error", e);
        }
    }

    private static class CpuTime {
        private String timestamp;

        private double time;

        public CpuTime(String timestamp, int time) {
            this.timestamp = timestamp;
            this.time = ((double) time) / 100;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public double getTime() {
            return time;
        }

        @Override
        public String toString() {
            return "CpuTime{" +
                    "maxRunningMs='" + timestamp + '\'' +
                    ", time=" + time +
                    '}';
        }
    }
}
