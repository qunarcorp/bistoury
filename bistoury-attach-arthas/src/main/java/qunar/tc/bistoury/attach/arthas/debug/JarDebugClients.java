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

package qunar.tc.bistoury.attach.arthas.debug;

import qunar.tc.bistoury.instrument.client.common.InstrumentInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: leix.xie
 * @date: 2019/2/28 10:51
 * @describe：
 */
public class JarDebugClients {
    private static volatile JarDebugClient client;
    private static AtomicBoolean init = new AtomicBoolean(false);

    public static JarDebugClient getInstance() {
        if (client != null) {
            return client;
        }
        throw new IllegalStateException("jar decompiler not available");
    }

    public static JarDebugClient create(InstrumentInfo instrumentInfo) {
        if (init.compareAndSet(false, true)) {
            client = new JarDebugClient(instrumentInfo);
            return client;
        } else {
            throw new IllegalStateException("jar decompiler client already created");
        }
    }
}
