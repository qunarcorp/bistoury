package qunar.tc.bistoury.agent.task.profiler;

import qunar.tc.bistoury.agent.common.task.AgentGlobalTaskFactory;
import qunar.tc.bistoury.common.NamedThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen created on 19-11-28 下午5:25
 */
public class ProfilerFileCleanTaskFactory implements AgentGlobalTaskFactory {

    private static final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("profiler-file-clean-task", true));

    @Override
    public void start() {
        executor.scheduleAtFixedRate(new TaskRunner(), 0, 1, TimeUnit.DAYS);
    }
}