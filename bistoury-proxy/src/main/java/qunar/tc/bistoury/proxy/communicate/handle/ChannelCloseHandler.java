package qunar.tc.bistoury.proxy.communicate.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 19:19
 */
@ChannelHandler.Sharable
public class ChannelCloseHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ChannelCloseHandler.class);

    private final String name;

    public ChannelCloseHandler(String name) {
        this.name = name;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        closeChannel(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            logger.warn("{} channel is idle, {}", name, ctx.channel());
            closeChannel(ctx.channel());
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("{} netty error, {}", name, ctx.channel(), cause);
        closeChannel(ctx.channel());
    }

    private void closeChannel(Channel channel) {
        channel.close();
    }
}
