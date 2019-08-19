package qunar.tc.bistoury.commands.arthas;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import qunar.tc.bistoury.agent.common.AgentConstants;
import qunar.tc.bistoury.agent.common.util.AgentUtils;
import qunar.tc.bistoury.agent.common.util.NetWorkUtils;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;

/**
 * @author xkrivzooh
 * @since 2019/8/19
 */
public class ArthasTelnetPortHelper {

    public static int getTelnetPort(String nullableAppCode) {
        if (!AgentUtils.supporGetPidFromProxy()) {
            MetaStore sharedMetaStore = MetaStores.getSharedMetaStore();
            sharedMetaStore.put(AgentConstants.TELNET_CONNECT_PORT, String.valueOf(TelnetConstants.TELNET_CONNECTION_PORT));
            return TelnetConstants.TELNET_CONNECTION_PORT;
        }

        Preconditions.checkArgument(!Strings.isNullOrEmpty(nullableAppCode), "appCode必须不能为空");
        MetaStore appMetaStore = MetaStores.getAppMetaStore(nullableAppCode);

        int availablePort = NetWorkUtils.getAvailablePort();
        appMetaStore.put(AgentConstants.TELNET_CONNECT_PORT, String.valueOf(availablePort));
        return availablePort;
    }
}
