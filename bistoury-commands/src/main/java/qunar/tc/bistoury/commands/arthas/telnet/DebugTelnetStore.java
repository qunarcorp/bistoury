package qunar.tc.bistoury.commands.arthas.telnet;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;

/**
 * @author zhenyu.nie created on 2018 2018/11/28 19:28
 */
public class DebugTelnetStore extends AbstractTelnetStore {

    private static final TelnetStore INSTANCE = new DebugTelnetStore();

    public static TelnetStore getInstance() {
        return INSTANCE;
    }

    private DebugTelnetStore() {

    }

    @Override
    protected Telnet doCreateTelnet(TelnetClient client) throws IOException {
        return new DebugTelnet(client);
    }
}
