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
import com.google.common.util.concurrent.SettableFuture;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.cpujstack.KvUtils;
import qunar.tc.bistoury.agent.common.job.BytesJob;
import qunar.tc.bistoury.agent.common.job.ContinueResponseJob;
import qunar.tc.bistoury.agent.common.kv.KvDb;
import qunar.tc.bistoury.agent.common.util.DateUtils;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/1/15 11:09
 */
public class ThreadNumTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(ThreadNumTask.class);

    private final SettableFuture<Integer> future = SettableFuture.create();

    private final String id;

    private final long maxRunningMs;

    private final KvDb kvDb;

    private final DateTime start;

    private final DateTime end;

    private final ResponseHandler handler;

    public ThreadNumTask(String id, long maxRunningMs, KvDb kvDb, DateTime start, DateTime end, ResponseHandler handler) {
        this.id = id;
        this.maxRunningMs = maxRunningMs;
        this.kvDb = kvDb;
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
    public ContinueResponseJob createJob() {
        return new Job();
    }

    @Override
    public ListenableFuture<Integer> getResultFuture() {
        return future;
    }

    private class Job extends BytesJob {

        private Job() {
            super(id, handler, future);
        }

        @Override
        protected byte[] getBytes() throws Exception {
            List<ThreadNum> threadNums = Lists.newArrayList();

            DateTime time = start;
            while (!time.isAfter(end)) {
                String timestamp = DateUtils.TIME_FORMATTER.print(time);
                int threadNum = getThreadNum(timestamp);
                if (threadNum > 0) {
                    threadNums.add(new ThreadNum(timestamp, threadNum));
                }
                time = time.plusMinutes(1);
            }

            Map<String, Object> map = Maps.newHashMap();

            map.put("type", "threadNum");
            map.put("threadNums", threadNums);
            map.put("start", DateUtils.TIME_FORMATTER.print(start));
            map.put("end", DateUtils.TIME_FORMATTER.print(end));
            return JacksonSerializer.serializeToBytes(map);
        }

        @Override
        public ListeningExecutorService getExecutor() {
            return AgentRemotingExecutor.getExecutor();
        }
    }

    private int getThreadNum(String timestamp) {
        String key = KvUtils.getThreadNumKey(timestamp);
        String value = kvDb.get(key);
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    private static class ThreadNum {
        private String timestamp;

        private int num;

        public ThreadNum(String timestamp, int num) {
            this.timestamp = timestamp;
            this.num = num;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public int getNum() {
            return num;
        }

        @Override
        public String toString() {
            return "ThreadNum{" +
                    "maxRunningMs='" + timestamp + '\'' +
                    ", num=" + num +
                    '}';
        }
    }
}
