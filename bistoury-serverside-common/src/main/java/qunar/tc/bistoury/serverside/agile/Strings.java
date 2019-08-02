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

package qunar.tc.bistoury.serverside.agile;


public final class Strings {

    /**
     * 通常情况下请不要使用构造方法创建这个类的实例.
     */
    public Strings() {
    }

    /**
     * 判断参数是否为null或空字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 把文字转换为 boolean 型, 当str 为 true,yes,on,1 时返回 true 否则返回false . 当str为空时返回def
     *
     * @param str boolean string
     * @param def default value
     * @return
     */
    public static boolean getBoolean(String str, boolean def) {

        if (isEmpty(str)) {
            return def;
        }
        str = str.trim().toUpperCase();
        return "TRUE".equals(str) || "YES".equals(str) || "ON".equals(str) || "1".equals(str);
    }
}
