package qunar.tc.bistoury.ui.dao;

import qunar.tc.bistoury.serverside.support.AppServer;

import java.util.List;

/**
 * @author leix.xie
 * @date 2019/7/2 14:18
 * @describe
 */
public interface AppServerDao {
    List<AppServer> getAppServerByAppCode(String appCode);

    AppServer getAppServerByIp(String ip);

    AppServer getAppServerByServerId(String serverId);

    int addAppServer(AppServer appServer);

    int updateAppServer(AppServer appServer);

    int changeAutoJMapHistoEnable(String serverId, boolean enable);

    int changeAutoJStackEnable(String serverId, boolean enable);

    int deleteAppServerByIp(String ip);

    int deleteAppServerByServerId(String serverId);
}
