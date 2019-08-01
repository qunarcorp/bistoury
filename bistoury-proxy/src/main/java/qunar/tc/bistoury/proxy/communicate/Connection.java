package qunar.tc.bistoury.proxy.communicate;

import com.google.common.util.concurrent.ListenableFuture;
import qunar.tc.bistoury.remoting.protocol.Datagram;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 18:28
 */
public interface Connection {

    ListenableFuture<WriteResult> write(Datagram message);

    ListenableFuture<Void> closeFuture();

    boolean isActive();

    void close();
}
