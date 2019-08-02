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

package qunar.tc.bistoury.serverside.util;

import com.google.common.base.Strings;

public class JDBCPathUtil {

    public static final String getJdbcPath() {
        String conf = System.getProperty("bistoury.conf");
        if (Strings.isNullOrEmpty(conf)) {
            return "classpath:jdbc.properties";
        }
        return "file:" + conf + "/jdbc.properties";
    }
}
