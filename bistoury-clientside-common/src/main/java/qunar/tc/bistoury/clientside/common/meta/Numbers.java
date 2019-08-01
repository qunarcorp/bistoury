package qunar.tc.bistoury.clientside.common.meta;

/**
 * @author: leix.xie
 * @date: 2019/3/15 15:14
 * @describe：
 */
public class Numbers {
    private Numbers() {
    }

    /**
     * 10进制整数的容错转换,无法确认的情况下返回 0
     *
     * @param str 整数的字符串表现形式
     * @return int
     * @see qunar.tc.bistoury.clientside.common.meta.Numbers#toInt(String, int);
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

    /**
     * 转换 ip 地址为规律的数字型, 如 202106000020,58083130165
     *
     * @param ip 字符型ip,如: 192.168.2.1
     * @return
     */
    public static long ipNiceNumber(String ip) {

        char[] chars = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'};
        char[] tokens = ip.toCharArray();

        int len = tokens.length - 1;
        int j = 11;
        char m;

        for (int i = 0; i <= len; i++) {
            m = ip.charAt(len - i);
            if (m != '.') {
                chars[j] = m;
                j--;
            } else {
                if (j != (11 - (11 - j) / 3 * 3))
                    j = 8 - (11 - j) / 3 * 3;
            }
        }
        return Long.parseLong(new String(chars));
    }
}
