package qunar.tc.bistoury.proxy.communicate.agent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 19:44
 */
@Service
public class DefaultAgentConnectionStore implements AgentConnectionStore {

    private final ConcurrentMap<String, AgentConnection> connections = Maps.newConcurrentMap();

    @Override
    public AgentConnection register(String agentId, int agentVersion, Channel channel) {
        DefaultAgentConnection agentConnection = new DefaultAgentConnection(agentId, agentVersion, channel);
        AgentConnection oldConnection = connections.get(agentId);
        if (!Objects.equals(oldConnection, agentConnection)) {
            oldConnection = connections.put(agentId, agentConnection);
            agentConnection.init();
            agentConnection.closeFuture().addListener(() -> connections.remove(agentId, agentConnection), MoreExecutors.directExecutor());
            if (oldConnection != null && !Objects.equals(oldConnection, agentConnection)) {
                oldConnection.close();
            }
            return agentConnection;
        } else {
            return oldConnection;
        }
    }

    @Override
    public Optional<AgentConnection> getConnection(String agentId) {
        AgentConnection agentConnection = connections.get(agentId);
        if (agentConnection != null) {
            return Optional.of(agentConnection);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Map<String, AgentConnection> getAgentConnection() {
        return ImmutableMap.copyOf(connections);
    }

    @Override
    public Map<String, AgentConnection> searchConnection(String agentId) {
        Map<String, AgentConnection> connection = getAgentConnection();
        Map<String, AgentConnection> result = Maps.filterKeys(connection, key -> key.indexOf(agentId) >= 0);
        return result;
    }
}
