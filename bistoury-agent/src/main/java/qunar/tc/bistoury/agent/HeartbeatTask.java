package qunar.tc.bistoury.agent;

import com.google.common.util.concurrent.MoreExecutors;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.NamedThreadFactory;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenyu.nie created on 2018 2018/10/25 19:40
 */
class HeartbeatTask {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatTask.class);

    private static final ScheduledExecutorService executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("bistoury-agent-heartbeat", true)));

    private final long heartbeatSec;

    private final Datagram heartbeatRequest;

    public HeartbeatTask(long heartbeatSec) {
        this.heartbeatSec = heartbeatSec;
        heartbeatRequest = RemotingBuilder.buildAgentRequest(ResponseCode.RESP_TYPE_HEARTBEAT.getCode(), null);
    }

    public void start(final Channel channel, final AtomicBoolean running) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (running.get()) {
                    channel.writeAndFlush(heartbeatRequest).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                logger.error("send heartbeat error, {}", channel);
                            } else {
                                logger.debug("send heartbeat, {}", channel);
                            }
                        }
                    });

                    executor.schedule(this, heartbeatSec, TimeUnit.SECONDS);
                }
            }
        });
    }
}
