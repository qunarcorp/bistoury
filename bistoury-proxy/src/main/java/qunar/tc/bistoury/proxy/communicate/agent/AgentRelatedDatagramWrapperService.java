package qunar.tc.bistoury.proxy.communicate.agent;

import java.util.Map;

import javax.annotation.Resource;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import qunar.tc.bistoury.application.api.AppServerPidInfoService;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.protocol.AgentRelatedDatagramConstants;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author xkrivzooh
 * @since 2019/8/16
 */
@Service
public class AgentRelatedDatagramWrapperService {

	@Resource
	private AppServerPidInfoService appServerPidInfoService;

	//其实此处可以考虑基于datagram的类型来看情况是否需要加pid信息，待优化吧
	public Datagram wrap(String appCode, Datagram datagram, AgentInfo agentInfo) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(appCode), "appCode不能为空");
		Preconditions.checkNotNull(datagram, "datagram不能为空");
		RemotingHeader header = datagram.getHeader();
		Preconditions.checkNotNull(header);
		Preconditions.checkNotNull(agentInfo, "agentInfo不能为空");

		Map<String, String> properties = header.getProperties();
		properties.put(AgentRelatedDatagramConstants.APP_CODE_HEADER, appCode);

		Map<String, Integer> appPidMapping = appServerPidInfoService.queryPidInfo(agentInfo.getAgentServerIp());
		if (!CollectionUtils.isEmpty(appPidMapping)) {
			properties.put(AgentRelatedDatagramConstants.AGENT_SERVER_PID_INFO_HEADER, JacksonSerializer.serialize(appPidMapping));
		}
		return datagram;
	}

	public Datagram wrap(Datagram datagram, AgentInfo agentInfo) {
		Preconditions.checkNotNull(datagram, "datagram不能为空");
		RemotingHeader header = datagram.getHeader();
		Preconditions.checkNotNull(header);
		Preconditions.checkNotNull(agentInfo, "agentInfo不能为空");

		Map<String, String> properties = header.getProperties();
		Map<String, Integer> appPidMapping = appServerPidInfoService.queryPidInfo(agentInfo.getAgentServerIp());
		if (!CollectionUtils.isEmpty(appPidMapping)) {
			properties.put(AgentRelatedDatagramConstants.AGENT_SERVER_PID_INFO_HEADER, JacksonSerializer.serialize(appPidMapping));
		}
		return datagram;
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
