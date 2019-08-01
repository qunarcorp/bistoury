package qunar.tc.bistoury.commands;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.netty.Processor;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.List;

/**
 * @author zhenyu.nie created on 2019 2019/1/10 19:21
 */
public class MetaRefreshTipProcessor implements Processor<String> {

    private static final Logger logger = LoggerFactory.getLogger(MetaRefreshTipProcessor.class);

    private static final byte[] EMPTY_BYTES = new byte[]{};

    public MetaRefreshTipProcessor() {
    }

    @Override
    public List<Integer> types() {
        return ImmutableList.of(CommandCode.REQ_TYPE_REFRESH_TIP.getCode());
    }

    @Override
    public void process(RemotingHeader header, String command, ResponseHandler handler) {
        logger.info("meta refresh receive tip");
        handler.handle(CommandCode.REQ_TYPE_REFRESH_AGENT_INFO.getCode(), EMPTY_BYTES);
    }
}
