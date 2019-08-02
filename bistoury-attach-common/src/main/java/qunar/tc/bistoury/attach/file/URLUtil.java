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

package qunar.tc.bistoury.attach.file;

import com.google.common.base.Strings;

/**
 * @author leix.xie
 * @date 2019-07-29 15:31
 * @describe
 */
public class URLUtil {

    public static String removeProtocol(final String url) {
        if (Strings.isNullOrEmpty(url)) {
            return url;
        }
        if (url.startsWith("jar:file:")) {
            return url.substring(9);
        } else if (url.startsWith("file:")) {
            return url.substring(5);
        } else {
            return url;
        }
    }
}
