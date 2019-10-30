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

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.job.BytesJob;
import qunar.tc.bistoury.agent.common.job.ContinueResponseJob;
import qunar.tc.bistoury.agent.common.util.Response;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.command.MonitorCommand;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: leix.xie
 * @date: 2019/1/9 15:27
 * @describe：
 */
public class QMonitorQueryTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(QMonitorQueryTask.class);

    private static final QMonitorStore Q_MONITOR_STORE = QMonitorStore.getInstance();
    private static final String TYPE_LIST = "list";
    private static final String TYPE_LATEST = "latest";

    private final String id;
    private final MonitorCommand command;
    private final ResponseHandler handler;
    private final long maxRunningMs;

    private final SettableFuture<Integer> future = SettableFuture.create();

    public QMonitorQueryTask(String id, MonitorCommand command, ResponseHandler handler, long maxRunningMs) {
        this.id = id;
        this.command = command;
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
            return getResponseBytes();
        }

        @Override
        public ListeningExecutorService getExecutor() {
            return AgentRemotingExecutor.getExecutor();
        }
    }

    private byte[] dealCommand() {
        String type = command.getType();
        if (TYPE_LIST.equals(type)) {
            Response response = Q_MONITOR_STORE.reportList(command.getName(), command.getStartTime(), command.getEndTime());
            return handlerSuccess(response);
        } else if (TYPE_LATEST.equals(type)) {
            Response response = Q_MONITOR_STORE.reportLatest(command.getName(), command.getQuery());
            return handlerSuccess(response);
        } else {
            throw new IllegalArgumentException("illegal type: " + type);
        }
    }

    private byte[] getResponseBytes() {
        try {
            return dealCommand();
        } catch (Throwable e) {
            logger.error("qmonitor query error, {}", command, e);
            return handlerError("qmonitor query error, " + e.getClass().getName() + ", " + e.getMessage());
        }
    }

    private byte[] handlerSuccess(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "qmonitorquery");
        result.put("code", 0);
        result.put("data", data);
        return toBytes(result);
    }

    private byte[] handlerError(String errorMsg) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "qmonitorquery");
        result.put("code", -1);
        result.put("message", errorMsg);
        return toBytes(result);
    }

    private byte[] toBytes(Map<String, Object> result) {
        return JacksonSerializer.serializeToBytes(result);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getMaxRunningMs() {
        return maxRunningMs;
    }
}
