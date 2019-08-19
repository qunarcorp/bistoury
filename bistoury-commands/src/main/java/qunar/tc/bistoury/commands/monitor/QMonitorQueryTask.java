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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.util.Response;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.command.MonitorCommand;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author: leix.xie
 * @date: 2019/1/9 15:27
 * @describe：
 */
public class QMonitorQueryTask implements Task {
    private static final Logger logger = LoggerFactory.getLogger(QMonitorQueryTask.class);
    private static final ListeningExecutorService agentExecutor = AgentRemotingExecutor.getExecutor();
    private final QMonitorStore qMonitorStore;
    private static final String TYPE_LIST = "list";
    private static final String TYPE_LATEST = "latest";
    private String id;
    private MonitorCommand command;
    private ResponseHandler handler;
    private long maxRunningMs;
    private volatile ListenableFuture<Integer> future;
    private final String nullableAppCode;

    public QMonitorQueryTask(String id, MonitorCommand command, ResponseHandler handler, long maxRunningMs, String nullableAppCode) {
        this.id = id;
        this.command = command;
        this.handler = handler;
        this.maxRunningMs = maxRunningMs;
        this.nullableAppCode = nullableAppCode;
        this.qMonitorStore = QMonitorStore.getInstance(this.nullableAppCode);
    }

    @Override
    public ListenableFuture<Integer> execute() {
        this.future = agentExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                try {
                    queryMonitor();
                } catch (Throwable e) {
                    logger.error("qmonitor query error", e);
                    handlerError("qmonitor query error, " + e.getClass().getName() + ", " + e.getMessage());
                }
                return null;
            }
        });
        return this.future;
    }

    private void queryMonitor() {
        final String type = command.getType();
        if (TYPE_LIST.equals(type)) {
            Response response = qMonitorStore.reportList(command.getName(), command.getStartTime(), command.getEndTime());
            handlerSuccess(response);
        } else if (TYPE_LATEST.equals(type)) {
            Response response = qMonitorStore.reportLatest(command.getName(), command.getQuery());
            handlerSuccess(response);
        } else {
            throw new IllegalArgumentException("illegal type: " + type);
        }
    }

    private void handlerSuccess(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "qmonitorquery");
        result.put("code", 0);
        result.put("data", data);
        handlerResult(result);
    }

    private void handlerError(String errorMsg) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "qmonitorquery");
        result.put("code", -1);
        result.put("message", errorMsg);
        handlerResult(result);
    }

    private void handlerResult(Map<String, Object> result) {
        handler.handle(JacksonSerializer.serialize(result));
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
    public void cancel() {
        try {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
        } catch (Exception e) {
            logger.error("cancel monitor query task error", e);
        }
    }
}
