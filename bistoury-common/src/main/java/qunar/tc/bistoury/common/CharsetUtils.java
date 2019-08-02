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

package qunar.tc.bistoury.common;

import com.google.common.base.Strings;

import java.nio.charset.Charset;

/**
 * @author yiqun.fan create on 17-7-6.
 */
public class CharsetUtils {
    public static final Charset UTF8 = Charset.forName("utf-8");

    private static final byte[] EMPTY_BYTES = new byte[0];

    public static byte[] toUTF8Bytes(final String s) {
        return Strings.isNullOrEmpty(s) ? EMPTY_BYTES : s.getBytes(UTF8);
    }

    public static String toUTF8String(final byte[] bs) {
        return bs == null || bs.length == 0 ? "" : new String(bs, UTF8);
    }
}
