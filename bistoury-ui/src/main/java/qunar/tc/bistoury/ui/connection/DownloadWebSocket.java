package qunar.tc.bistoury.ui.connection;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;

import javax.servlet.http.HttpServletResponse;
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
    private HttpServletResponse response;
    private OutputStream outputStream;
    private String command;
    private String filename;
    private boolean downloadResponse = false;

    public DownloadWebSocket(URI serverUri, CountDownLatch latch, OutputStream outputStream, HttpServletResponse response, final String filename, final String command) {
        super(serverUri);
        this.latch = latch;
        this.response = response;
        this.command = command;
        this.filename = filename;
        this.outputStream = outputStream;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
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
        latch.countDown();
    }

    @Override
    public void onError(Exception ex) {
        logger.error("web socket error", ex);
        latch.countDown();
    }

    private void handleResult(ByteBuffer buffer) {
        try {
            long id = buffer.getLong();
            int type = buffer.getInt();
            int ip = buffer.getInt();
            int length = buffer.getInt();

            if (type == ResponseCode.RESP_TYPE_EXCEPTION.getOldCode()) {
                response.setStatus(500);
                outputStream.write(buffer.array(), 20, length);
                outputStream.flush();
                latch.countDown();
            } else if (type != ResponseCode.RESP_TYPE_ALL_END.getOldCode() && type != ResponseCode.RESP_TYPE_SINGLE_END.getOldCode()) {
                handleDownloadResponse();
                outputStream.write(buffer.array(), 20, length);
            } else {
                //如果是空文件，会直接走这个逻辑，如果不对response进行设置，下载不到文件
                handleDownloadResponse();
                outputStream.flush();
                latch.countDown();
            }
        } catch (Throwable t) {
            latch.countDown();
            logger.error("download fail", t);
        }
    }

    //将返回数据设置为文件
    private void handleDownloadResponse() throws IOException {
        if (!downloadResponse) {
            downloadResponse = true;
            outputStream = response.getOutputStream();
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + filename);
        }
    }

}
