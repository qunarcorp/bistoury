package qunar.tc.bistoury.attach.common;

import com.taobao.middleware.logger.Logger;
import com.taobao.middleware.logger.util.MessageUtil;

/**
 * @author cai.wen created on 2019/11/5 15:24
 */
public class BistouryLoggerHelper {

    private static final Logger logger = BistouryLoggger.getLogger();

    public static void info(String formatText, Object... args) {
        logger.info(formatText, args);
    }

    public static void error(Throwable t, String formatText, Object... args) {
        String msg = formatMessage(formatText, args);
        logger.error(null, msg, t);
    }

    public static void warn(String formatText, Object... args) {
        logger.warn(formatText, args);
    }

    private static String formatMessage(String formatText, Object... args) {
        return MessageUtil.formatMessage(formatText, args);
    }
}
