package qunar.tc.bistoury.commands;

import com.google.common.base.Charsets;

/**
 * @author zhenyu.nie created on 2018 2018/10/15 16:38
 */
public class ProcessorUtils {

    public static String getString(byte[] input) {
        if (input == null) {
            return null;
        }
        return new String(input, Charsets.UTF_8);
    }
}
