package qunar.tc.bistoury.proxy.communicate.agent;

import qunar.tc.bistoury.proxy.communicate.Connection;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 14:18
 */
public interface AgentConnection extends Connection {

    String getAgentId();

    int getVersion();

}
