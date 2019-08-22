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

package qunar.tc.bistoury.serverside.common;

import org.apache.curator.framework.state.ConnectionStateListener;

import java.util.List;

/**
 * @author leix.xie
 * @date 2019-08-09 12:07
 * @describe
 */
public interface ZKClient {
    void deletePath(String path) throws Exception;

    List<String> getChildren(String path) throws Exception;

    boolean checkExist(String path);

    void addPersistentNode(String path) throws Exception;

    String addEphemeralNode(String path) throws Exception;

    void addConnectionChangeListener(ConnectionStateListener listener);

    void incrementReference();

    void close();
}
