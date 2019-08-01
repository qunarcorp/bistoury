package qunar.tc.bistoury.proxy.communicate.ui;

import io.netty.channel.Channel;
import qunar.tc.bistoury.proxy.communicate.AbstractConnection;

import java.util.Objects;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 11:09
 */
public class DefaultUiConnection extends AbstractConnection implements UiConnection {

    private final Channel channel;

    public DefaultUiConnection(Channel channel) {
        super("ui", channel);
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultUiConnection that = (DefaultUiConnection) o;
        return Objects.equals(channel, that.channel);
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel);
    }
}
