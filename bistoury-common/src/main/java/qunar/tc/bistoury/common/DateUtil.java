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

import org.joda.time.DateTime;

/**
 * @author: leix.xie
 * @date: 2019/1/7 11:38
 * @describe：
 */
public class DateUtil {
    public static long getMinute() {
        return transformToMinute(System.currentTimeMillis());
    }

    public static long transformToMinute(long timestamp) {
        return transformToMinuteWithDateTime(timestamp).getMillis();
    }

    public static DateTime transformToMinuteWithDateTime(long timestamp) {
        return new DateTime(timestamp).minuteOfDay().roundFloorCopy();
    }

    public static long plusDays(long timestamp, int days) {
        return transformToMinuteWithDateTime(timestamp).plusDays(days).getMillis();
    }
}
