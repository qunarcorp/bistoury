package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.serverside.support.AppServer;

import java.util.List;

/**
 * @author leix.xie
 * @date 2019/7/2 15:13
 * @describe
 */
public interface AppServerService {
    List<AppServer> getAppServerByAppCode(String appCode);

    int changeAutoJMapHistoEnable(String serverId, boolean enable);

    int changeAutoJStackEnable(String serverId, boolean enable);

    int deleteAppServerByServerId(String serverId);

    int saveAppServer(AppServer appServer);
}
