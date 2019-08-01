package qunar.tc.bistoury.proxy.communicate.agent.handler;

import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.remoting.protocol.Datagram;

import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/5/14 17:13
 */
@ChannelHandler.Sharable
public class AgentMessageHandler extends SimpleChannelInboundHandler<Datagram> {

    private static final Logger logger = LoggerFactory.getLogger(AgentMessageHandler.class);

    private final Map<Integer, AgentMessageProcessor> processorMap;

    public AgentMessageHandler(List<AgentMessageProcessor> processors) {
        ImmutableMap.Builder<Integer, AgentMessageProcessor> builder = new ImmutableMap.Builder<>();
        for (AgentMessageProcessor processor : processors) {
            for (int code: processor.codes()) {
                builder.put(code, processor);
            }
        }
        processorMap = builder.build();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Datagram message) throws Exception {
        int code = message.getHeader().getCode();
        AgentMessageProcessor messageProcessor = processorMap.get(code);
        if (messageProcessor == null) {
            message.release();
            logger.warn("can not process message code [{}], {}", code, ctx.channel());
            return;
        }

        messageProcessor.process(ctx, message);
    }
}
