package qunar.tc.bistoury.commands.heapHisto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author: leix.xie
 * @date: 2018/12/10 14:36
 * @describe：
 */
public class HeapHistoTask implements Task {
    private static final Logger logger = LoggerFactory.getLogger(HeapHistoTask.class);

    private static final ListeningExecutorService agentExecutor = AgentRemotingExecutor.getExecutor();

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private final static HeapHistoStore HEAPHISTO_STORE = HeapHistoStore.getInstance();

    private final String id;

    private final int pid;

    private final long maxRunningMs;

    private final String param;

    private final ResponseHandler handler;

    private final long selectTimestamp;

    private volatile ListenableFuture<Integer> future;

    public HeapHistoTask(String id, int pid, final long selectTimestamp, final String param, ResponseHandler handler, long maxRunningMs) {
        this.id = id;
        this.pid = pid;
        this.selectTimestamp = selectTimestamp;
        this.param = param;
        this.handler = handler;
        this.maxRunningMs = maxRunningMs;
    }

    @Override
    public ListenableFuture<Integer> execute() {
        this.future = agentExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                try {
                    List<HistogramBean> histogramBeans;
                    if (selectTimestamp > 0) {
                        histogramBeans = HEAPHISTO_STORE.getHistogramBean(selectTimestamp);
                    } else {
                        HeapHistoBeanHandle heapHistoBeanHandle = new HeapHistoBeanHandle(param, pid);
                        histogramBeans = heapHistoBeanHandle.heapHisto();
                    }
                    handlerSuccess(histogramBeans);
                } catch (Exception e) {
                    logger.error("get heap histo error", e);
                    handlerError("get heap histo error, " + e.getClass().getName() + ", " + e.getMessage());
                }
                return null;
            }
        });
        return future;
    }

    private void handlerSuccess(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "heapHisto");
        result.put("code", 0);
        result.put("data", data);
        handlerResult(result);
    }

    private void handlerError(String errorMsg) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "heapHisto");
        result.put("code", -1);
        result.put("message", errorMsg);
        handlerResult(result);
    }

    private void handlerResult(Map<String, Object> result) {
        try {
            handler.handle(MAPPER.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            logger.error("heap histo serialize error");
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

    @Override
    public void cancel() {
        try {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
        } catch (Exception e) {
            logger.error("cancel heap histo task error", e);
        }
    }
}