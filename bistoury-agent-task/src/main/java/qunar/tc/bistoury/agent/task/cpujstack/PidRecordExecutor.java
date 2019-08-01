package qunar.tc.bistoury.agent.task.cpujstack;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Map;

/**
 * @author cai.wen
 */
public interface PidRecordExecutor {

    ListenableFuture<Map<Integer, Double>> execute(int pid);
}
