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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.job.ResponseJobStore;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

/**
 * @author zhenyu.nie created on 2019 2019/5/28 15:40
 */
public class TaskProcessor implements Processor<Object> {

    private static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);

    private final ResponseJobStore jobStore;

    private final TaskStore taskStore;

    private final Map<Integer, TaskFactory<?>> taskFactories;

    public TaskProcessor(ResponseJobStore jobStore, TaskStore taskStore, List<TaskFactory> taskFactories) {
        this.jobStore = jobStore;
        this.taskStore = taskStore;

        ImmutableMap.Builder<Integer, TaskFactory<?>> builder = new ImmutableMap.Builder<>();
        for (TaskFactory<?> factory : taskFactories) {
            for (Integer type : factory.codes()) {
                builder.put(type, factory);
            }
        }
        this.taskFactories = builder.build();
    }

    @Override
    public List<Integer> types() {
        return ImmutableList.copyOf(taskFactories.keySet());
    }

    @Override
    public void process(RemotingHeader header, final Object command, final ResponseHandler handler) {
        try {
            final int code = header.getCode();
            final String id = header.getId();
            final TaskFactory<?> factory = taskFactories.get(code);
            Preconditions.checkState(factory != null);
            logger.info("receive {} command, id [{}], command [{}]", factory.name(), id, command);

            RunnableTask task = createTask(factory, header, command, handler);
            if (task == null) {
                return;
            }

            ListenableFuture<Integer> future = task.execute();
            future.addListener(new Runnable() {
                @Override
                public void run() {
                    taskStore.finish(id);
                }
            }, MoreExecutors.directExecutor());

            Futures.addCallback(future, new FutureCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    int eof = result == null ? 0 : result;
                    handler.handleEOF(eof);
                    logger.info("{} command finish, id [{}], command [{}]", factory.name(), id, command);
                }

                @Override
                public void onFailure(Throwable t) {
                    if (t instanceof CancellationException) {
                        logger.info("{} command canceled, id [{}]", factory.name(), id);
                        return;
                    }

                    handler.handleError(t);
                    logger.error("{} command error, id [{}], command [{}]", factory.name(), id, command, t);
                }
            }, AgentRemotingExecutor.getExecutor());
        } catch (Exception e) {
            handler.handleError(e);
            logger.error("task process error", e);
        }
    }

    @SuppressWarnings("unchecked")
    private RunnableTask createTask(TaskFactory factory, RemotingHeader header, Object command, ResponseHandler handler) {
        Task task = factory.create(header, command, handler);
        RunnableTask runnableTask = RunnableTasks.wrap(jobStore, task);
        if (taskStore.register(runnableTask)) {
            return runnableTask;
        } else {
            return null;
        }
    }
}
