package qunar.tc.bistoury.proxy.communicate;

import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.UiConnection;

import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 14:29
 */
public interface SessionManager {

    Session create(RequestData requestData, AgentConnection agentConnection, UiConnection uiConnection);

    Session getSession(String id);

    Set<Session> getSessionByUiConnection(UiConnection connection);
}
