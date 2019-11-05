package qunar.tc.bistoury.proxy.util;

import qunar.tc.bistoury.proxy.communicate.ui.AgentServerInfo;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;

import java.util.List;

/**
 * @author leix.xie
 * @date 2019/11/5 15:32
 * @describe
 */
public class DownloadDirUtils {
    private static String downloadOtherStr;
    private static String downloadDumpStr;
    private static final String ALL_DIR = "all";
    private static final String LOG_DIR = "log";
    private static final String DUMP_DIR = "dump";

    static {
        DynamicConfigLoader.<DynamicConfig>load("global.properties", false).addListener(config -> {
            downloadDumpStr = config.getString("download.dump.dir", "/tmp/bistoury/qjtools/qjdump");
            downloadOtherStr = config.getString("download.other.dir", "");
        });
    }

    public static String composeDownloadDir(final List<AgentServerInfo> serverInfos, final String type) {
        if (serverInfos == null || serverInfos.isEmpty()) {
            return "";
        }
        String logdir = serverInfos.iterator().next().getLogdir();
        if (ALL_DIR.equalsIgnoreCase(type)) {
            return logdir + "," + downloadDumpStr + "," + downloadOtherStr;
        } else if (LOG_DIR.equalsIgnoreCase(type)) {
            return logdir;
        } else if (DUMP_DIR.equalsIgnoreCase(type)) {
            return downloadDumpStr;
        } else {
            return downloadOtherStr;
        }
    }
}
