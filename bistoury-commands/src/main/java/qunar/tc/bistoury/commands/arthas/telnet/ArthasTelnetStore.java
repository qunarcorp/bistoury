package qunar.tc.bistoury.commands.arthas.telnet;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;

/**
 * @author zhenyu.nie created on 2018 2018/11/28 19:26
 */
public class ArthasTelnetStore extends AbstractTelnetStore {

    private static final ArthasTelnetStore INSTANCE = new ArthasTelnetStore();

    public static TelnetStore getInstance() {
        return INSTANCE;
    }

    private ArthasTelnetStore() {

    }

    @Override
    protected Telnet doCreateTelnet(TelnetClient client) throws IOException {
        return new ArthasTelnet(client);
    }
}
