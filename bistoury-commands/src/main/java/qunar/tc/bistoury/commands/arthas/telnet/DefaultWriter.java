package qunar.tc.bistoury.commands.arthas.telnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;

/**
 * @author zhenyu.nie created on 2019 2019/10/14 14:18
 */
class DefaultWriter implements Writer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultWriter.class);

    private final ResponseHandler delegate;

    DefaultWriter(ResponseHandler delegate) {
        this.delegate = delegate;
    }

    public void write(byte[] data) {
        while (true) {
            if (!delegate.isActive()) {
                logger.warn("send channel is not active");
                throw new IllegalStateException("send channel is not active");
            } else if (delegate.isWritable()) {
                delegate.handle(data);
                break;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.warn("", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}