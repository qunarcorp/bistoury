package qunar.tc.bistoury.proxy.communicate;

import com.google.common.util.concurrent.ListenableFuture;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.UiConnection;
import qunar.tc.bistoury.remoting.protocol.Datagram;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 14:18
 */
public interface Session {

    enum State {
        finish, broken
    }

    void writeToUi(Datagram message);

    void writeToAgent(Datagram message);

    String getId();

    RequestData getRequestData();

    AgentConnection getAgentConnection();

    UiConnection getUiConnection();

    boolean finish();

    boolean broken();

    ListenableFuture<State> getEndState();
}
