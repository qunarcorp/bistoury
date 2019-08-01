package qunar.tc.bistoury.agent;

import com.google.common.collect.ImmutableList;
import qunar.tc.bistoury.agent.common.task.AgentGlobalTaskFactory;

import java.util.List;
import java.util.ServiceLoader;

/**
 * @author zhenyu.nie created on 2019 2019/1/8 17:15
 */
public class AgentGlobalTaskInitializer {

    private static boolean init = false;

    public static synchronized void init() {
        if (!init) {
            List<AgentGlobalTaskFactory> tasks = ImmutableList.copyOf(ServiceLoader.load(AgentGlobalTaskFactory.class));
            for (AgentGlobalTaskFactory task : tasks) {
                task.start();
            }
            init = true;
        }
    }
}
