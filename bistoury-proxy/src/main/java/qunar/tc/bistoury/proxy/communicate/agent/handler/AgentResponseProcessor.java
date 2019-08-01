package qunar.tc.bistoury.proxy.communicate.agent.handler;

import com.google.common.collect.ImmutableSet;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.proxy.communicate.Session;
import qunar.tc.bistoury.proxy.communicate.SessionManager;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;

import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/5/14 18:12
 */
@Service
public class AgentResponseProcessor implements AgentMessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AgentResponseProcessor.class);

    @Autowired
    private SessionManager sessionManager;

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(
                ResponseCode.RESP_TYPE_CONTENT.getCode(),
                ResponseCode.RESP_TYPE_EXCEPTION.getCode(),
                ResponseCode.RESP_TYPE_SINGLE_END.getCode()
        );
    }

    @Override
    public void process(ChannelHandlerContext ctx, Datagram message) {
        String id = message.getHeader().getId();
        Session session = sessionManager.getSession(id);
        if (session != null) {
            session.writeToUi(message);
        } else {
            logger.warn("id [{}] can not get session, write response fail, {}", id, ctx.channel());
        }
    }
}
