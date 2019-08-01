package qunar.tc.bistoury.remoting.netty;

import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.pid.PidUtils;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.remoting.command.CommandSerializer;
import qunar.tc.bistoury.remoting.protocol.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2018 2018/10/22 17:30
 */
@ChannelHandler.Sharable
public class RequestHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Map<Integer, Processor> processorMap;
    private final CodeTypeMappingStore codeTypeMappingStore = CodeTypeMappingStores.getInstance();

    public RequestHandler(List<Processor> processors) {
        ImmutableMap.Builder<Integer, Processor> builder = new ImmutableMap.Builder<>();
        for (Processor<?> processor : processors) {
            for (Integer type : processor.types()) {
                builder.put(type, processor);
            }
        }
        processorMap = builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("request process error", cause);
        String requestId = ctx.channel().attr(AgentConstants.attributeKey).get();
        if (requestId != null) {
            RemotingHeader requestHeader = new RemotingHeader();
            requestHeader.setId(requestId);
            String errorMessage = "request process error, " + cause.getClass().getName() + ": " + cause.getMessage();
            ResponseWriter.getInstance().writeEOF(ctx, errorMessage, requestHeader);
        }
        ctx.channel().close();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        final Datagram datagram = (Datagram) msg;
        RemotingHeader header = datagram.getHeader();
        int code = header.getCode();
        String id = header.getId();

        if (code != ResponseCode.RESP_TYPE_HEARTBEAT.getCode()) {
            logger.info("agent receive request: id={}, sourceIp={}, code={}", id, ctx.channel().remoteAddress(), code);
        }
        final ResponseHandler handler = NettyExecuteHandler.of(header, ctx);
        ctx.channel().attr(AgentConstants.attributeKey).set(id);

        Processor processor = processorMap.get(code);
        if (processor == null) {
            handler.handleError(new IllegalArgumentException("unknown code [" + code + "]"));
            return;
        }

        String command = CommandSerializer.readCommand(datagram.getBody());
        int index = command.indexOf(BistouryConstants.FILL_PID);
        if (index >= 0) {
            int pid = PidUtils.getPid();
            if (pid < 0) {
                handler.handleError(ErrorCode.PID_ERROR.getCode());
                handler.handleEOF();
                return;
            }
            command = command.replace(BistouryConstants.FILL_PID, String.valueOf(pid));
        }

        Class<?> commandType = codeTypeMappingStore.getMappingType(code);

        processor.process(header, commandType.cast(CommandSerializer.deserializeCommand(command, commandType)), handler);
    }
}
