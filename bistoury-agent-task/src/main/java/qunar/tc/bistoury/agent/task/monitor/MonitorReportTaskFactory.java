package qunar.tc.bistoury.agent.task.monitor;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import qunar.tc.bistoury.agent.common.task.AgentGlobalTaskFactory;
import qunar.tc.bistoury.common.NamedThreadFactory;
import qunar.tc.bistoury.remoting.netty.MonitorReceiver;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author: leix.xie
 * @date: 2019/1/8 18:09
 * @describe：
 */
public class MonitorReportTaskFactory implements AgentGlobalTaskFactory {

    private static final ListeningScheduledExecutorService executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("qmonitor-report-task", true)));

    private final MonitorReceiver receiver = new MonitorReceiver();

    @Override
    public void start() {
        TaskRunner taskRunner = new TaskRunner(receiver);
        executor.scheduleAtFixedRate(taskRunner, 0, 1, TimeUnit.MINUTES);
    }
}
