package qunar.tc.bistoury.commands.cpujstack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
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
 * @author zhenyu.nie created on 2019 2019/1/15 11:09
 */
public class ThreadNumTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(ThreadNumTask.class);

    private static final ListeningExecutorService agentExecutor = AgentRemotingExecutor.getExecutor();

    private volatile ListenableFuture<Integer> future;

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
        handler.handle(JacksonSerializer.serializeToBytes(map));
        return null;
    }

    private int getThreadNum(String timestamp) {
        String key = KvUtils.getThreadNumKey(timestamp);
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
            logger.error("cancel thread num task error", e);
        }
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
