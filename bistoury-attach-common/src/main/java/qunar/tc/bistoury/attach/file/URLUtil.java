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
