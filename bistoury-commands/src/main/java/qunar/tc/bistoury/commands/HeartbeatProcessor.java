package qunar.tc.bistoury.commands;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.netty.Processor;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;

import java.util.List;

/**
 * @author zhenyu.nie created on 2018 2018/10/29 19:05
 */
public class HeartbeatProcessor implements Processor<String> {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatProcessor.class);

    @Override
    public List<Integer> types() {
        return ImmutableList.of(ResponseCode.RESP_TYPE_HEARTBEAT.getCode());
    }

    @Override
    public void process(RemotingHeader header, String command, ResponseHandler handler) {
        if (logger.isDebugEnabled()) {
            logger.debug("receive heartbeat response, {}", header.getId());
        }
    }
}
