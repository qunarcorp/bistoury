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

package qunar.tc.bistoury.proxy.generator;

import org.springframework.stereotype.Service;
import qunar.tc.bistoury.remoting.util.LocalHost;
import qunar.tc.bistoury.serverside.util.ServerManager;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leix.xie
 * @date 2019/5/13 14:32
 * @describe
 */
@Service
public class SessionIdGenerator implements IdGenerator {
    private static final String SPLITTER = ".";
    private static final int[] codex = {2, 3, 5, 6, 8, 9, 19, 11, 12, 14, 15, 17, 18};
    private static final AtomicInteger AUTO_INCREMENT_ID = new AtomicInteger(1);
    private static final String LOCAL_HOST = LocalHost.getLocalHost();
    private static final int PID = ServerManager.getPid();

    public String generateId() {
        StringBuilder sb = new StringBuilder(45);
        long time = System.currentTimeMillis();
        String ts = new Timestamp(time).toString();

        for (int idx : codex)
            sb.append(ts.charAt(idx));
        sb.append(SPLITTER).append(LOCAL_HOST);
        sb.append(SPLITTER).append(PID);
        sb.append(SPLITTER).append(AUTO_INCREMENT_ID.getAndIncrement());
        return sb.toString();
    }
}
