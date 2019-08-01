package qunar.tc.bistoury.agent.task.cpujstack;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import qunar.tc.bistoury.agent.task.proc.ProcessStateCalculator;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen
 * @date 19-1-22
 */
public class MomentCpuTimeRecordExecutor implements PidRecordExecutor {

    private static final int intervalMillis = 4000;

    private final ListeningScheduledExecutorService listeningScheduledExecutorService;
    private final ProcessStateCalculator processStateCalculator = ProcessStateCalculator.getInstance();

    public MomentCpuTimeRecordExecutor(ListeningScheduledExecutorService scheduledExecutorService) {
        this.listeningScheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public ListenableFuture<Map<Integer, Double>> execute(final int pid) {
        processStateCalculator.startRecordFullStat(pid);
        return listeningScheduledExecutorService.schedule(new Callable<Map<Integer, Double>>() {
            @Override
            public Map<Integer, Double> call() {
                return processStateCalculator.endRecordFullStat(pid);
            }
        }, intervalMillis, TimeUnit.MILLISECONDS);
    }
}
