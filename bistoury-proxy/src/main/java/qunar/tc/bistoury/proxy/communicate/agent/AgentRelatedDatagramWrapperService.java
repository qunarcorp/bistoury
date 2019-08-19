package qunar.tc.bistoury.proxy.communicate.agent;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.agent.common.AgentConstants;
import qunar.tc.bistoury.application.api.AppServerPidInfoService;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author xkrivzooh
 * @since 2019/8/16
 */
@Service
public class AgentRelatedDatagramWrapperService {

    @Resource
    private AppServerPidInfoService appServerPidInfoService;

    public Datagram wrap(String appCode, Datagram datagram, AgentInfo agentInfo) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(appCode), "appCode不能为空");
        Preconditions.checkNotNull(datagram, "datagram不能为空");
        RemotingHeader header = datagram.getHeader();
        Preconditions.checkNotNull(header);
        Preconditions.checkNotNull(agentInfo, "agentInfo不能为空");

        addAppCodeToHeader(appCode, header);
        addPidRelatedConfigToHeader(agentInfo, header);

        return datagram;
    }

    public Datagram addPidRelatedConfigToHeader(Datagram datagram, AgentInfo agentInfo) {
        Preconditions.checkNotNull(datagram, "datagram不能为空");
        RemotingHeader header = datagram.getHeader();
        Preconditions.checkNotNull(header);
        Preconditions.checkNotNull(agentInfo, "agentInfo不能为空");

        addPidRelatedConfigToHeader(agentInfo, header);
        return datagram;
    }

    private void addPidRelatedConfigToHeader(AgentInfo agentInfo, RemotingHeader header) {
        Map<String, String> properties = Maps.newHashMap(header.getProperties());
        try {
            Map<String, Integer> appPidMapping = appServerPidInfoService.queryPidInfo(agentInfo.getAgentServerIp());
            properties.put(AgentConstants.SUPPORT_GET_PID_FROM_PROXY, Boolean.TRUE.toString());
            properties.put(AgentConstants.AGENT_SERVER_PID_INFO, JacksonSerializer.serialize(appPidMapping));
        } catch (UnsupportedOperationException e) {
            properties.put(AgentConstants.SUPPORT_GET_PID_FROM_PROXY, Boolean.FALSE.toString());
        }
        header.setProperties(properties);
    }

    private void addAppCodeToHeader(String appCode, RemotingHeader header) {
        Map<String, String> properties = Maps.newHashMap(header.getProperties());
        properties.put(AgentConstants.APP_CODE, appCode);
        header.setProperties(properties);
    }

    public static class AgentInfo {
        private final String agentServerIp;

        public AgentInfo(String agentServerIp) {
            this.agentServerIp = agentServerIp;
        }

        String getAgentServerIp() {
            return agentServerIp;
        }
    }

}
