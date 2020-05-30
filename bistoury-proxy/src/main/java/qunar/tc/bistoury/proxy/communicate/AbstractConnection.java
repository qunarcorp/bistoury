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

package qunar.tc.bistoury.proxy.communicate;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.remoting.protocol.Datagram;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 12:22
 */
public abstract class AbstractConnection implements Connection {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConnection.class);

    private final String name;
    private final Channel channel;

    private final SettableFuture<Void> closeFuture = SettableFuture.create();

    public AbstractConnection(String name, Channel channel) {
        this.name = name;
        this.channel = channel;
    }

    public void init() {
        channel.closeFuture().addListener((f) -> closeFuture.set(null));
    }

    @Override
    public ListenableFuture<WriteResult> write(Datagram message) {
        SettableFuture<WriteResult> result = SettableFuture.create();
        channel.writeAndFlush(message).addListener(future -> {
            if (future.isSuccess()) {
                result.set(WriteResult.success);
            } else {
                logger.warn("{} connection write fail, {}, {}", name, channel, message);
                result.set(WriteResult.fail);
            }
        });
        return result;
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public boolean isWritable() {
        return channel.isWritable();
    }

    @Override
    public ListenableFuture<Void> closeFuture() {
        return closeFuture;
    }

    @Override
    public void close() {
        logger.info("close {} channel {}", name, channel);
        channel.close();
    }
}
