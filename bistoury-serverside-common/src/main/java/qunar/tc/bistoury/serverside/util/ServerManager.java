package qunar.tc.bistoury.serverside.util;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.Throwables;
import qunar.tc.bistoury.serverside.agile.LocalHost;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import java.lang.management.ManagementFactory;
import java.util.Set;

/**
 * @author leix.xie
 * @date 2019/7/11 20:25
 * @describe
 */
public class ServerManager {
    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);

    public static void printServerConfig() {
        System.out.println();
        System.out.println("Server Config");
        System.out.println("--------------------------------");
        System.out.println("Server IP    : " + LocalHost.getLocalHost());
        System.out.println("Server Host  : " + LocalHost.getHostName());
        System.out.println("Server PID   : " + getPid());
        System.out.println("Server Port  : " + getTomcatPort());
        System.out.println();
    }

    public static int getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return Integer.valueOf(name.substring(0, name.indexOf(64)));
    }

    public static int getTomcatPort() {
        return Integer.valueOf(getTomcatPortBySystemProperty());
    }

    private static String getTomcatPortBySystemProperty() {
        String port = System.getProperty("bistoury.tomcat.pory");
        if (Strings.isNullOrEmpty(port)) {
            port = getTomcatPortByMxBean();
        }
        return port;
    }

    private static String getTomcatPortByMxBean() {
        String tomcatPort = "-1";
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            if (server != null) {
                Set<ObjectName> objectNames = server.queryNames(new ObjectName("*:type=Connector,*"),
                        Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
                tomcatPort = objectNames.iterator().next().getKeyProperty("port");
            }

        } catch (Exception e) {
            logger.error("get tomcat port error", e);
            throw Throwables.propagate(e);
        }
        return tomcatPort;
    }
}
