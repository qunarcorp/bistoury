package qunar.tc.bistoury.attach.arthas.Util;

import qunar.tc.bistoury.clientside.common.meta.MetaStore;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/3/14 17:02
 */
public class AgentConfig {

    private MetaStore metaStore;

    public AgentConfig(MetaStore metaStore) {
        this.metaStore = metaStore;
    }

    public synchronized boolean update(Map<String, String> input) {
        if (!metaStore.getAgentInfo().equals(input)) {
            metaStore.update(input);
            return true;
        }
        return false;
    }
}
