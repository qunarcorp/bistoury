package qunar.tc.bistoury.ui.connection;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

/**
 * @author leix.xie
 * @date 2019/11/5 10:45
 * @describe
 */

public class DownloadWebSocket extends WebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(DownloadWebSocket.class);

    private CountDownLatch latch;
    private OutputStream outputStream;
    private String command;

    public DownloadWebSocket(URI serverUri, CountDownLatch latch, OutputStream outputStream, final String command) {
        super(serverUri);
        this.latch = latch;
        this.outputStream = outputStream;
        this.command = command;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("command: {}", command);
        this.send(command);
    }

    @Override
    public void onMessage(String message) {
        //proxy netty返回的是二进制数据，不走这个逻辑
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        handleResult(bytes);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("close reason: {}", reason);
        latch.countDown();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        latch.countDown();
    }

    private final void handleResult(ByteBuffer buffer) {
        try {
            long id = buffer.getLong();
            int type = buffer.getInt();
            int ip = buffer.getInt();
            int length = buffer.getInt();
            if (type != ResponseCode.RESP_TYPE_ALL_END.getOldCode() && type != ResponseCode.RESP_TYPE_SINGLE_END.getOldCode()) {
                outputStream.write(buffer.array(), 20, 20 + length);
            } else {
                outputStream.flush();
                latch.countDown();
            }
        } catch (IOException e) {
            logger.error("download fail", e);
        }
    }
}
