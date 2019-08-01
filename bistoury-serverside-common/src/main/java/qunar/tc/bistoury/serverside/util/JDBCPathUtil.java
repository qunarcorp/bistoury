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
