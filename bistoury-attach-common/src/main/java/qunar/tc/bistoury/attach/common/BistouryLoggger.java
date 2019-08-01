package qunar.tc.bistoury.attach.common;

import com.taobao.middleware.logger.Level;
import com.taobao.middleware.logger.Logger;
import com.taobao.middleware.logger.LoggerFactory;
import com.taobao.middleware.logger.support.LogLog;

/**
 * @author: leix.xie
 * @date: 2019/1/17 16:17
 * @describe：
 */
public class BistouryLoggger {
    private static final Logger jsonLogger;

    private static final Logger logger;

    static {
        LogLog.setQuietMode(true);

        int maxBackupIndex = Integer.valueOf(System.getProperty("bistoury.log.max.backup.index", "7"));

        jsonLogger = LoggerFactory.getLogger("bistoury-json");
        jsonLogger.activateAppenderWithTimeAndSizeRolling("bistoury-json", "bistoury-json.log", "UTF-8", "100MB", "yyyy-MM-dd", maxBackupIndex);
        jsonLogger.setLevel(Level.INFO);
        jsonLogger.setAdditivity(false);

        logger = LoggerFactory.getLogger("bistoury");
        logger.activateAppenderWithTimeAndSizeRolling("bistoury", "bistoury.log", "UTF-8", "100MB", "yyyy-MM-dd", maxBackupIndex);
        logger.setLevel(Level.INFO);
        logger.setAdditivity(false);
    }

    public static Logger getJsonLogger() {
        return jsonLogger;
    }

    public static Logger getLogger() {
        return logger;
    }
}
