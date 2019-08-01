package qunar.tc.bistoury.proxy.communicate.ui;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 11:38
 */
@Service
public class DefaultUiConnectionStore implements UiConnectionStore {

    private final ConcurrentMap<Channel, UiConnection> connections = Maps.newConcurrentMap();

    @Override
    public UiConnection register(Channel channel) {
        DefaultUiConnection connection = new DefaultUiConnection(channel);
        UiConnection oldConnection = connections.putIfAbsent(channel, connection);
        if (oldConnection != null) {
            return oldConnection;
        }

        connection.init();
        connection.closeFuture().addListener(() -> connections.remove(channel), MoreExecutors.directExecutor());
        return connection;
    }

    @Override
    public Optional<UiConnection> getConnection(Channel channel) {
        UiConnection connection = connections.get(channel);
        if (connection != null) {
            return Optional.of(connection);
        }
        return Optional.absent();
    }
}
