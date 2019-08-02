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

package qunar.tc.bistoury.remoting.netty;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.NamedThreadFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhenyu.nie created on 2019 2019/5/28 16:52
 */
public class DefaultTaskStore implements TaskStore {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTaskStore.class);

    private static final ScheduledExecutorService clearExecutor =
            Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("bistoury-task-clear"));

    private static final int CLEAR_INTERVAL_SEC = 10;

    private final ConcurrentMap<String, WrapTask> tasks = Maps.newConcurrentMap();

    private volatile boolean close = false;

    public DefaultTaskStore() {
        clearExecutor.schedule(clearRunningTooLongTask, CLEAR_INTERVAL_SEC, TimeUnit.SECONDS);
    }

    private final Runnable clearRunningTooLongTask = new Runnable() {
        @Override
        public void run() {
            if (close) {
                return;
            }

            try {
                long currentTime = System.currentTimeMillis();
                for (Map.Entry<String, WrapTask> entry : tasks.entrySet()) {
                    WrapTask wrapTask = entry.getValue();
                    Task task = wrapTask.getTask();
                    if (currentTime - wrapTask.getTimestamp() > task.getMaxRunningMs()) {
                        logger.warn("try cancel task [{}], running too long times", task.getId());
                        task.cancel();
                        tasks.remove(entry.getKey());
                    }
                }
            } finally {
                clearExecutor.schedule(clearRunningTooLongTask, CLEAR_INTERVAL_SEC, TimeUnit.SECONDS);
            }
        }
    };

    @Override
    public boolean register(Task task) {
        synchronized (this) {
            if (close) {
                return false;
            }

            WrapTask old = tasks.putIfAbsent(task.getId(), new WrapTask(task, System.currentTimeMillis()));
            return old == null;
        }
    }

    @Override
    public void finish(String id) {
        tasks.remove(id);
    }

    @Override
    public void cancel(final String id) {
        WrapTask task = tasks.putIfAbsent(id, cancelStubTask);
        if (task != null) {
            task.getTask().cancel();
            tasks.remove(id);
        } else {
            clearExecutor.schedule(new Runnable() {
                @Override
                public void run() {
                    tasks.remove(id, cancelStubTask);
                }
            }, 1, TimeUnit.MINUTES);
        }
    }

    @Override
    public void close() {
        synchronized (this) {
            close = true;
        }

        logger.warn("close task store, cancel all task");
        for (WrapTask wrapTask : tasks.values()) {
            wrapTask.getTask().cancel();
        }
    }

    private static final WrapTask cancelStubTask = new WrapTask(new Task() {
        @Override
        public String getId() {
            return "";
        }

        @Override
        public long getMaxRunningMs() {
            return Long.MAX_VALUE;
        }

        @Override
        public ListenableFuture<Integer> execute() {
            return null;
        }

        @Override
        public void cancel() {

        }
    }, 0);

    private static class WrapTask {

        private final Task task;

        private final long timestamp;

        private WrapTask(Task task, long timestamp) {
            this.task = task;
            this.timestamp = timestamp;
        }

        public Task getTask() {
            return task;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
