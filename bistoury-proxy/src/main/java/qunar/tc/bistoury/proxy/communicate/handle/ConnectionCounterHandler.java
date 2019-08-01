package qunar.tc.bistoury.proxy.communicate.handle;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.serverside.metrics.Metrics;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 19:08
 */
@ChannelHandler.Sharable
public class ConnectionCounterHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionCounterHandler.class);

    private final AtomicInteger count = new AtomicInteger();

    private final String name;

    public ConnectionCounterHandler(String name) {
        Metrics.gauge("netty_connection_" + name + "_active", count::doubleValue);
        this.name = name;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("{} {} connected", name, ctx.channel().remoteAddress());
        count.incrementAndGet();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("{} {} disconnected", name, ctx.channel().remoteAddress());
        count.decrementAndGet();
        super.channelInactive(ctx);
    }
}
