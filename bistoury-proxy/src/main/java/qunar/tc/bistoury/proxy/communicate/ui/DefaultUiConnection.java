/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.proxy.communicate.ui;

import com.google.common.collect.Sets;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.proxy.communicate.AbstractConnection;
import qunar.tc.bistoury.proxy.communicate.WritableListener;

import java.util.Objects;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 11:09
 */
public class DefaultUiConnection extends AbstractConnection implements UiConnection {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUiConnection.class);

    private final Channel channel;

    private final Set<WritableListener> listeners = Sets.newHashSet();

    private boolean writable;

    public DefaultUiConnection(Channel channel) {
        super("ui", channel);
        this.channel = channel;
        this.writable = channel.isWritable();
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public synchronized void setWritable(boolean writable) {
        if (this.writable == writable) {
            return;
        }

        logger.info("channel writable change, {}->{}, {}", this.writable, writable, channel);
        this.writable = writable;
        notifyWritableChange(writable);
    }

    @Override
    public synchronized void addWritableListener(WritableListener listener) {
        listeners.add(listener);
        listener.onChange(this.writable);
    }

    @Override
    public synchronized void removeWritableListener(WritableListener listener) {
        listeners.remove(listener);
    }

    private void notifyWritableChange(boolean writable) {
        for (WritableListener listener : listeners) {
            listener.onChange(writable);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultUiConnection that = (DefaultUiConnection) o;
        return Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel);
    }
}
