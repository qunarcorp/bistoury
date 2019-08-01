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
