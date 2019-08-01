package qunar.tc.bistoury.agent.common.config;

import qunar.tc.bistoury.clientside.common.meta.MetaStore;

/**
 * @author zhenyu.nie created on 2019 2019/3/25 14:37
 */
public class AgentConfig {


    private final MetaStore metaStore;

    public AgentConfig(MetaStore metaStore) {
        this.metaStore = metaStore;
    }

    public boolean isHeapHistoOn() {
        return metaStore.getBooleanProperty("heapJMapHistoOn", false);
    }

    public int getHeapHistoStoreSize() {
        return metaStore.getIntProperty("heapHisto.store.size", 100);
    }

    public int getAgentInfoRefreshInterval() {
        return metaStore.getIntProperty("agent.refresh.interval.min", 10);
    }

    public boolean isCpuJStackOn() {
        return metaStore.getBooleanProperty("cpuJStackOn", false);
    }

}
