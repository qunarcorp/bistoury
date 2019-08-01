package qunar.tc.bistoury.proxy.communicate.agent;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.Optional;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 19:42
 */
public interface AgentConnectionStore {

    AgentConnection register(String agentId, int agentVersion, Channel channel);

    Optional<AgentConnection> getConnection(String agentId);

    Map<String, AgentConnection> getAgentConnection();

    Map<String,AgentConnection> searchConnection(String agentId);
}
