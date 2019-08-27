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

package qunar.tc.bistoury.serverside.database;


import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

/**
 * @author leix.xie
 * @date 2019/8/26 16:32
 * @describe
 */
public class H2DataBeseUtil {
    private static final String portPath = "/tmp/bistoury/h2port.conf";

    public String getUrl() {
        try {
            String port = getPort();
            return "jdbc:h2:tcp://localhost:" + port + "//tmp/bistoury/h2/bistoury;MODE=MYSQL;TRACE_LEVEL_SYSTEM_OUT=2;AUTO_SERVER=TRUE;";
        } catch (Exception e) {
            System.err.println("获取h2端口失败，使用默认端口：9092\n" + e);
        }
        return "jdbc:h2:tcp://localhost//tmp/bistoury/h2/bistoury;MODE=MYSQL;TRACE_LEVEL_SYSTEM_OUT=2;AUTO_SERVER=TRUE;";
    }

    public static String getPort() throws IOException {
        return Files.readFirstLine(new File(portPath), Charsets.UTF_8);
    }
}
