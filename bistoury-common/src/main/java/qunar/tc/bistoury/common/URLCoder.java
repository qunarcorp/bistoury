package qunar.tc.bistoury.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author zhenyu.nie created on 2018 2018/11/26 15:38
 */
public class URLCoder {

    public static String encode(String input) {
        if (input == null) {
            return null;
        }

        try {
            return URLEncoder.encode(input, "utf8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String decode(String input) {
        if (input == null) {
            return null;
        }

        try {
            return URLDecoder.decode(input, "utf8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
