package qunar.tc.bistoury.common;

import com.google.common.base.Strings;

import java.nio.charset.Charset;

/**
 * @author yiqun.fan create on 17-7-6.
 */
public class CharsetUtils {
    public static final Charset UTF8 = Charset.forName("utf-8");

    private static final byte[] EMPTY_BYTES = new byte[0];

    public static byte[] toUTF8Bytes(final String s) {
        return Strings.isNullOrEmpty(s) ? EMPTY_BYTES : s.getBytes(UTF8);
    }

    public static String toUTF8String(final byte[] bs) {
        return bs == null || bs.length == 0 ? "" : new String(bs, UTF8);
    }
}
