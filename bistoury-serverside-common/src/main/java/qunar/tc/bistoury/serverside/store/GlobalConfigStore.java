package qunar.tc.bistoury.serverside.store;

import qunar.tc.bistoury.serverside.agile.Conf;
import qunar.tc.bistoury.serverside.agile.LocalHost;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.util.ServerManager;

/**
 * @author cai.wen created on 2019/9/3 17:39
 */
public class GlobalConfigStore {

    private static int websocketPort = -1;
    private static int tomcatPort = -1;

    static {
        initProxyPort();
    }

    public static String getProxyNode() {
        return LocalHost.getLocalHost() + ":" + tomcatPort + ":" + websocketPort;
    }

    private static void initProxyPort() {
        Conf conf = Conf.fromMap(DynamicConfigLoader.load("global.properties").asMap());
        websocketPort = conf.getInt("server.port", -1);
        tomcatPort = ServerManager.getTomcatPort();
    }
}
