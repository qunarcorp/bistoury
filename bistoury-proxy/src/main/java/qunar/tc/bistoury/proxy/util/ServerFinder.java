package qunar.tc.bistoury.proxy.util;

import qunar.tc.bistoury.serverside.support.AppServer;

import java.util.List;

public interface ServerFinder {

    List<AppServer> findAgents(String app);
}
