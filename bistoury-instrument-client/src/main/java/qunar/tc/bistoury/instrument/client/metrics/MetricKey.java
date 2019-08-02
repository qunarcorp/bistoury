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

package qunar.tc.bistoury.instrument.client.metrics;

import com.google.common.base.Objects;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import java.util.regex.Pattern;

public class MetricKey {

    // 指标名
    final String name;

    private static final Interner<String> interner = Interners.newStrongInterner();

    private static final int MAX_TAG_STRING_LENGTH = 500;

    private static final String TAG_STRING_TOO_LONG = "tag_string_too_long";

    public MetricKey(String name) {
        this.name = interner.intern(normalize(name));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        MetricKey key = (MetricKey) obj;
        return Objects.equal(this.name, key.name);
    }

    @Override
    public String toString() {
        return name;
    }

    private static Pattern identifier = Pattern.compile("^[0-9a-zA-Z][0-9a-zA-Z_\\-\\.]*$");
    private static Pattern normalize = Pattern.compile("[^0-9a-zA-Z_\\-\\.\\,\\(\\)\\#\\[\\]]");

    public static boolean isIdentifier(String s) {
        return s != null && !s.isEmpty() && identifier.matcher(s).find();
    }

    /**
     * 转换非 数字、大小写字母、下划线、点、中横线为下划线
     * 
     * @param s
     * @return
     */
    public static String normalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        if (s.length() > MAX_TAG_STRING_LENGTH) {
            return TAG_STRING_TOO_LONG;
        }
        return normalize.matcher(s).replaceAll("_");
    }

}
