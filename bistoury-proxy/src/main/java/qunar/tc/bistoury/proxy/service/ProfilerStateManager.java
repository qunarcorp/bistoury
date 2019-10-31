package qunar.tc.bistoury.proxy.service;

import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;

/**
 * @author cai.wen created on 2019/10/30 16:54
 */
public interface ProfilerStateManager {

    void register(AgentConnection agentConnection, String command, String profilesId);

    void stop(String profilesId);
}
