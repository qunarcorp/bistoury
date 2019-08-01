package qunar.tc.bistoury.agent.task.cpujstack;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import qunar.tc.bistoury.agent.common.config.AgentConfig;
import qunar.tc.bistoury.agent.common.kv.KvDb;
import qunar.tc.bistoury.agent.common.kv.KvDbs;
import qunar.tc.bistoury.agent.common.task.AgentGlobalTaskFactory;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.common.NamedThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zhenyu.nie created on 2019 2019/1/8 17:29
 */
public class CpuJStackTaskFactory implements AgentGlobalTaskFactory {

    private static final ListeningScheduledExecutorService executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("cpu-jstack-task", true)));

    private static final KvDb kvDb = KvDbs.getKvDb();

    private static final AgentConfig agentConfig = new AgentConfig(MetaStores.getMetaStore());

    @Override
    public void start() {
        PidExecutor jstackExecutor = new JStackPidExecutor();
        PidRecordExecutor momentCpuTimePidExecutor = new MomentCpuTimeRecordExecutor(executor);
        TaskRunner taskRunner = new TaskRunner(agentConfig, kvDb, jstackExecutor, momentCpuTimePidExecutor);
        executor.scheduleAtFixedRate(taskRunner, 5, 60, TimeUnit.SECONDS);
    }
}
