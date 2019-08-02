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

/**
 * @author miao.yang
 */
public final class Numbers {

    private Numbers() {
    }

    /**
     * 10进制整数的容错转换,无法确认的情况下返回 0
     *
     * @param str 整数的字符串表现形式
     * @return int
     * @see Numbers#toInt(String, int);
     */
    public static int toInt(String str) {
        return toInt(str, 0);
    }

    /**
     * @param str 10进制整数的容错转换,无法确认的情况下返回 def
     * @param def 默认值
     * @return int
     * @see Integer#parseInt(String);
     */
    public static int toInt(String str, int def) {
        if (str == null)
            return def;
        try {
            return Integer.parseInt(str, 10);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 10进制整数的容错转换,无法确认的情况下返回 0
     *
     * @param str 整数的字符串表现形式
     * @return long
     * @see Numbers#toLong(String);
     */
    public static long toLong(String str) {
        return toLong(str, 0L);
    }

    /**
     * @param str 10进制整数的容错转换,无法确认的情况下返回 def
     * @param def 默认值
     * @return long
     * @see Long#parseLong(String);
     */
    public static long toLong(String str, long def) {
        if (str == null)
            return def;
        try {
            return Long.parseLong(str, 10);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * @param str 10进制容错转换,无法确认的情况下返回 0
     * @return
     */
    public static float toFloat(String str) {
        return toFloat(str, 0F);
    }

    /**
     * @param str 10进制容错转换,无法确认的情况下返回 def
     * @param def 默认值
     * @return
     */
    public static float toFloat(String str, float def) {
        if (str == null)
            return def;
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * @param str 10进制容错转换,无法确认的情况下返回 0
     * @return
     */
    public static double toDouble(String str) {
        return toDouble(str, 0D);
    }

    /**
     * @param str 10进制容错转换,无法确认的情况下返回 def
     * @param def 默认值
     * @return
     */
    public static double toDouble(String str, double def) {
        if (str == null)
            return def;
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

}