package qunar.tc.bistoury.serverside.util;

import qunar.tc.bistoury.serverside.agile.LocalHost;

import java.lang.management.ManagementFactory;

/**
 * @author leix.xie
 * @date 2019/7/11 20:25
 * @describe
 */
public class ServerManager {
    public static void printServerConfig() {
        System.out.println();
        System.out.println("Server Config");
        System.out.println("--------------------------------");
        System.out.println("Server IP    : " + LocalHost.getLocalHost());
        System.out.println("Server Host  : " + LocalHost.getHostName());
        System.out.println("Server PID   : " + getPid());
        System.out.println();
    }

    public static int getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return Integer.valueOf(name.substring(0, name.indexOf(64)));
    }
}
