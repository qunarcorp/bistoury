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

package qunar.tc.bistoury.commands.host;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.job.BytesJob;
import qunar.tc.bistoury.agent.common.job.ContinueResponseJob;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.io.IOException;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;

/**
 * @author: leix.xie
 * @date: 2018/11/21 11:18
 * @describe：
 */
public class ThreadInfoTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(ThreadInfoTask.class);

    private static final Integer ALL_THREADS_INFO = 0;

    private static final Integer THREAD_DETAIL = 1;

    private static final Integer DUMP_THREADS = 2;

    private static final Integer DEADLOCK_THREAD = 3;

    private static final String TYPE = "type";

    private static final String THREAD = "thread";

    private static final String THREADS = "threads";

    private final String id;

    private final int pid;

    private final long threadId;

    private final int commandType;

    private final int maxDepth;

    private final ResponseHandler handler;

    private final long maxRunningMs;

    private final SettableFuture<Integer> future = SettableFuture.create();

    public ThreadInfoTask(String id, int pid, long threadId, int commandType, int maxDepth, ResponseHandler handler, long maxRunningMs) {
        this.id = id;
        this.pid = pid;
        this.threadId = threadId;
        this.commandType = commandType;
        this.maxDepth = maxDepth;
        this.handler = handler;
        this.maxRunningMs = maxRunningMs;
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
            VirtualMachineUtil.VMConnector connect = VirtualMachineUtil.connect(pid);
            Map<String, Object> result = new HashMap<>();
            if (ALL_THREADS_INFO == commandType) {
                result.put(TYPE, "allThreadInfo");
                List<ThreadBrief> threads = getAllThreadsInfo(connect, result);
                result.put(THREADS, threads);
            } else if (THREAD_DETAIL == commandType) {
                result.put(TYPE, "threadDetail");
                ThreadInfo threadInfo = getThreadInfo(connect, result);
                result.put(THREAD, threadInfo);
            } else if (DUMP_THREADS == commandType) {
                result.put(TYPE, "threadDump");
                ThreadInfo[] threads = dump(connect, maxDepth, false);
                result.put(THREADS, threads);
            } else if (DEADLOCK_THREAD == commandType) {
                result.put(TYPE, "threadDeadLock");
                ThreadInfo[] threads = dump(connect, maxDepth, true);
                result.put(THREADS, threads);
            }
            return JacksonSerializer.serializeToBytes(result);
        }

        @Override
        public ListeningExecutorService getExecutor() {
            return AgentRemotingExecutor.getExecutor();
        }
    }

    private ThreadInfo[] dump(VirtualMachineUtil.VMConnector connect, int maxDepth, boolean onlyDeadLock) {
        try {
            ThreadMXBean threadBean = connect.getThreadMXBean();
            long[] ids;
            if (onlyDeadLock) {
                ids = threadBean.findDeadlockedThreads();
            } else {
                ids = threadBean.getAllThreadIds();
            }
            if (ids != null) {
                return threadBean.getThreadInfo(ids, maxDepth);
            } else {
                return new ThreadInfo[]{};
            }
        } catch (IOException e) {
            logger.error("dump thread error", e);
            return new ThreadInfo[]{};
        }
    }

    private ThreadInfo getThreadInfo(VirtualMachineUtil.VMConnector connect, Map<String, Object> result) {
        try {
            ThreadMXBean threadMXBean = connect.getThreadMXBean();
            long threadCpuTime = threadMXBean.getThreadCpuTime(threadId);
            result.put("cpuTime", threadCpuTime);
            return threadMXBean.getThreadInfo(threadId, maxDepth);
        } catch (IOException e) {
            logger.error("get thread info error", e);
            return null;
        }
    }

    private List<ThreadBrief> getAllThreadsInfo(VirtualMachineUtil.VMConnector connect, Map<String, Object> result) {
        List<ThreadBrief> threads = new ArrayList<>();
        long totalCpuTime = 0;
        try {
            ThreadMXBean threadMXBean = connect.getThreadMXBean();
            long[] ids = threadMXBean.getAllThreadIds();
            ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(ids);
            for (ThreadInfo info : threadInfos) {
                long thId = info.getThreadId();
                long cpuTime = threadMXBean.getThreadCpuTime(thId);
                ThreadBrief threadBrief = new ThreadBrief(info.getThreadId(), info.getThreadName(),
                        cpuTime, info.getThreadState());
                totalCpuTime += cpuTime;
                threads.add(threadBrief);
            }
            Collections.sort(threads, new Comparator<ThreadBrief>() {
                @Override
                public int compare(ThreadBrief o1, ThreadBrief o2) {
                    return Long.compare(o2.cpuTime, o1.cpuTime);
                }
            });
            result.put("totalCpuTime", totalCpuTime);
            return threads;
        } catch (IOException e) {
            logger.error("get all thread info error", e);
            return threads;
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getMaxRunningMs() {
        return maxRunningMs;
    }

    static class ThreadBrief {
        private long id;
        private String name;
        private long cpuTime;
        private Thread.State state;

        public ThreadBrief(long id, String name, long cpuTime, Thread.State state) {
            this.id = id;
            this.name = name;
            this.cpuTime = cpuTime;
            this.state = state;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public long getCpuTime() {
            return cpuTime;
        }

        public Thread.State getState() {
            return state;
        }
    }
}
