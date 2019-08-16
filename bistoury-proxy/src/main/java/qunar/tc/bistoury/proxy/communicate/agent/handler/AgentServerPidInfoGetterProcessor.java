package qunar.tc.bistoury.proxy.communicate.agent.handler;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.proxy.communicate.agent.AgentRelatedDatagramWrapperService;
import qunar.tc.bistoury.proxy.communicate.agent.AgentRelatedDatagramWrapperService.AgentInfo;
import qunar.tc.bistoury.proxy.generator.IdGenerator;
import qunar.tc.bistoury.proxy.util.ChannelUtils;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xkrivzooh
 * @since 2019/8/16
 */
@Service
public class AgentServerPidInfoGetterProcessor implements AgentMessageProcessor {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IdGenerator generator;

	@Autowired
	private AgentRelatedDatagramWrapperService agentRelatedDatagramWrapperService;

	@Override
	public Set<Integer> codes() {
		return ImmutableSet.of(CommandCode.REQ_TYPE_AGENT_SERVER_PID_GETTER.getCode());
	}

	@Override
	public void process(ChannelHandlerContext ctx, Datagram message) {
		logger.info("receiver agent server pid info get message");
		Datagram datagram = RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_REFRESH_AGENT_INFO.getCode(),
				generator.generateId(), null);
		agentRelatedDatagramWrapperService.wrap(datagram, new AgentInfo(ChannelUtils.getIp(ctx.channel())));
		ctx.writeAndFlush(datagram);
	}
}
