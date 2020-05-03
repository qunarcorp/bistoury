package qunar.tc.bistoury.ui.controller;

import com.google.common.util.concurrent.SettableFuture;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import qunar.tc.bistoury.remoting.protocol.ResponseCode;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * @author leix.xie
 * @date 2019/11/5 16:53
 * @describe
 */
public class DownloadFileControllerTest {
    public static void main(String[] args) {
        WebSocketClient client = null;
        final SettableFuture<Integer> future = SettableFuture.create();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            client = new WebSocketClient(new URI("ws://100.80.128.181:9881/ws")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("web socket open");
                    //System.out.println(this);
                    this.send("{\"0\":\"Hk/Ue+DxkKe8FhLktTbfIwh6m9gjC0NJieyZCW8Zleau/37qmrfkinjX4zpEEcklb1p8JpoliDbDuTegvjdUjdcIhaRu/2qWeh+ebG/ufmi+fCqzSYbxl3hdrdkNekR5Akc+YtqbPhBUj5qdnPsrk6l4Qfh4H0BiaddpOxbHv6Y=\",\"1\":\"HX9LpFWgrTUt7yBqS6+Gjc4ftPHi1+kj/W+XqTq0vdCp1SmKGGLTdHFzMLgeJyiaJl2bCyCoDeSsfvpWEWxi8Nk/sUCkaaid164zR80mAc+uvg55Cc/KkdHsMF2UJwIPHJghucJgUg+MqYt04j8na+n7ZWEYxFuz6jZhVQOspZueZvkvyEH2Cb5EoU5yt2gcOWciX+K7T/iG1j0z5Oc5ovaejuBurwwL/ompvD0vI4plPdTXqqfuQAPy3pQ6pwhq53cGBVk56U0=\"}");

                    future.set(0);
                }

                @Override
                public void onMessage(String message) {
                    //proxy netty返回的是二进制数据，不走这个逻辑
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    handleResult(bytes, latch);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("close");
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                    future.setException(ex);
                }
            };
            client.connect();
            while (!client.getReadyState().equals(ReadyState.OPEN)) {
                System.out.println("正在连接");
            }

            latch.await();
        } catch (Exception e) {

        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    private static final void handleResult(ByteBuffer buffer, CountDownLatch latch) {
        long id = buffer.getLong();
        int type = buffer.getInt();
        int ip = buffer.getInt();
        int length = buffer.getInt();
        if (type != ResponseCode.RESP_TYPE_ALL_END.getOldCode() && type != ResponseCode.RESP_TYPE_SINGLE_END.getOldCode()) {
            System.out.println(new String(Arrays.copyOfRange(buffer.array(), 20, 20 + length)));
        } else {
            latch.countDown();
        }
    }
}
