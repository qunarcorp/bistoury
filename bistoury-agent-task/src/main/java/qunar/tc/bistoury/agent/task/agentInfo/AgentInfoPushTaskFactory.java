package qunar.tc.bistoury.agent.task.agentInfo;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import qunar.tc.bistoury.agent.common.task.AgentGlobalTaskFactory;
import qunar.tc.bistoury.common.NamedThreadFactory;

import java.util.concurrent.Executors;

/**
 * @author: leix.xie
 * @date: 2019/2/25 17:55
 * @describe：
 */
public class AgentInfoPushTaskFactory implements AgentGlobalTaskFactory {
    private static final ListeningScheduledExecutorService executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("agent-info-push-task", true)));

    @Override
    public void start() {
        TaskRunner taskRunner = new TaskRunner(executor);
        executor.submit(taskRunner);
    }
}
