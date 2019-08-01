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
