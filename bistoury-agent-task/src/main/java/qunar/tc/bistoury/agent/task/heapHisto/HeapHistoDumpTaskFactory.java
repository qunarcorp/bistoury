package qunar.tc.bistoury.agent.task.heapHisto;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import qunar.tc.bistoury.agent.common.task.AgentGlobalTaskFactory;
import qunar.tc.bistoury.common.NamedThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author: leix.xie
 * @date: 2019/3/29 20:06
 * @describe：
 */
public class HeapHistoDumpTaskFactory implements AgentGlobalTaskFactory {
    private static final ListeningScheduledExecutorService executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("heaphisto-dump-task", true)));

    @Override
    public void start() {
        TaskRunner taskRunner = new TaskRunner();
        executor.scheduleAtFixedRate(taskRunner, 0, 1, TimeUnit.MINUTES);
    }
}
