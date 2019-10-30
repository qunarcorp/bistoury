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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: leix.xie
 * @date: 2018/12/10 14:36
 * @describe：
 */
public class HeapHistoTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(HeapHistoTask.class);

    private final static HeapHistoStore HEAPHISTO_STORE = HeapHistoStore.getInstance();

    private final String id;

    private final int pid;

    private final long maxRunningMs;

    private final String param;

    private final ResponseHandler handler;

    private final long selectTimestamp;

    private final SettableFuture<Integer> future = SettableFuture.create();

    public HeapHistoTask(String id, int pid, final long selectTimestamp, final String param, ResponseHandler handler, long maxRunningMs) {
        this.id = id;
        this.pid = pid;
        this.selectTimestamp = selectTimestamp;
        this.param = param;
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
            try {
                List<HistogramBean> histogramBeans;
                if (selectTimestamp > 0) {
                    histogramBeans = HEAPHISTO_STORE.getHistogramBean(selectTimestamp);
                } else {
                    HeapHistoBeanHandle heapHistoBeanHandle = new HeapHistoBeanHandle(param, pid);
                    histogramBeans = heapHistoBeanHandle.heapHisto();
                }
                return handlerSuccess(histogramBeans);
            } catch (Exception e) {
                logger.error("get heap histo error", e);
                return handlerError("get heap histo error, " + e.getClass().getName() + ", " + e.getMessage());
            }
        }

        @Override
        public ListeningExecutorService getExecutor() {
            return AgentRemotingExecutor.getExecutor();
        }
    }

    private byte[] handlerSuccess(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "heapHisto");
        result.put("code", 0);
        result.put("data", data);
        return toBytes(result);
    }

    private byte[] handlerError(String errorMsg) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "heapHisto");
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