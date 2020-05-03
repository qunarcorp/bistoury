package qunar.tc.bistoury.proxy.util;

import qunar.tc.bistoury.remoting.protocol.AgentServerInfo;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;

import java.util.List;

/**
 * @author leix.xie
 * @date 2019/11/5 15:32
 * @describe
 */
public class DownloadDirUtils {
    private static String defaultDownloadOtherStr;
    private static String defaultDownloadDumpStr;
    private static DynamicConfig dynamicConfig;

    private static final String DEFAULT_KEY_PREFIX = "default";
    private static final String DOWNLOAD_OTHER_KEY = ".download.other.dir";
    private static final String DOWNLOAD_DUMP_KEY = ".download.dump.dir";

    private static final String ALL_DIR = "all";
    private static final String LOG_DIR = "log";
    private static final String DUMP_DIR = "dump";

    static {
        DynamicConfigLoader.<DynamicConfig>load("download_dir_limit.properties", false).addListener(config -> {
            dynamicConfig = config;
            defaultDownloadDumpStr = dynamicConfig.getString(DEFAULT_KEY_PREFIX + DOWNLOAD_DUMP_KEY, "");
            defaultDownloadOtherStr = dynamicConfig.getString(DEFAULT_KEY_PREFIX + DOWNLOAD_OTHER_KEY, "");
        });
    }

    public static String composeDownloadDir(final String appCode, final List<AgentServerInfo> serverInfos, final String type) {
        if (serverInfos == null || serverInfos.isEmpty()) {
            return "";
        }
        String logdir = serverInfos.iterator().next().getLogdir();

        final String appDownloadDump = dynamicConfig.getString(appCode + DOWNLOAD_DUMP_KEY, defaultDownloadDumpStr);
        final String appDonnloadOther = dynamicConfig.getString(appCode + DOWNLOAD_OTHER_KEY, defaultDownloadOtherStr);

        if (ALL_DIR.equalsIgnoreCase(type)) {
            return logdir + "," + appDownloadDump + "," + appDonnloadOther;
        } else if (LOG_DIR.equalsIgnoreCase(type)) {
            return logdir;
        } else if (DUMP_DIR.equalsIgnoreCase(type)) {
            return appDownloadDump;
        } else {
            return appDonnloadOther;
        }
    }
}
