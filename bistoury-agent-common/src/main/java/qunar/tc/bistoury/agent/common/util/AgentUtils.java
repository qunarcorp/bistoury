package qunar.tc.bistoury.agent.common.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import qunar.tc.bistoury.agent.common.AgentConstants;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;

import java.util.Set;

import static qunar.tc.bistoury.agent.common.AgentConstants.SUPPORT_GET_PID_FROM_PROXY;

/**
 * @author xkrivzooh
 * @since 2019/8/19
 */
public class AgentUtils {

    public static final Splitter COMMA_SPLITTER = Splitter.on(",").trimResults();

    public static boolean supporGetPidFromProxy() {
        MetaStore sharedMetaStore = MetaStores.getSharedMetaStore();
        String supportedGetPidFromProxy = sharedMetaStore.getStringProperty(SUPPORT_GET_PID_FROM_PROXY, Boolean.FALSE.toString());
        return supportedGetPidFromProxy.equalsIgnoreCase(Boolean.TRUE.toString());
    }

    public static Set<String> getAppCodesDeployOnAgentServer() {
        Preconditions.checkState(supporGetPidFromProxy());
        MetaStore sharedMetaStore = MetaStores.getSharedMetaStore();
        String value = sharedMetaStore.getStringProperty(AgentConstants.APP_CODES_DEPLOY_ON_AGENT_SERVER_COMMA_SPLIT);
        if (Strings.isNullOrEmpty(value)) {
            return Sets.newHashSet();
        }
        return Sets.newHashSet(COMMA_SPLITTER.splitToList(value));
    }

}
