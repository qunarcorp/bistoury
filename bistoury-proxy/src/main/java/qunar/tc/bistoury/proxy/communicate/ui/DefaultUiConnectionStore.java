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

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
        return Optional.empty();
    }
}
