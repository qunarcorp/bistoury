package qunar.tc.bistoury.proxy.config;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 13:49
 */
public interface AgentInfoManager {

    ListenableFuture<Map<String, String>> getAgentInfo(String ip);

    void updateAgentInfo(List<String> agentIds);
}
