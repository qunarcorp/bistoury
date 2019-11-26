package qunar.tc.bistoury.attach.common;

import com.taobao.middleware.logger.util.MessageUtil;

/**
 * @author cai.wen created on 2019/11/5 15:24
 */
public class BistouryLoggerHelper {

    public static String formatMessage(String formatText, Object... args) {
        return MessageUtil.formatMessage(formatText, args);
    }
}
