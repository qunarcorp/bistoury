package qunar.tc.bistoury.proxy.config;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.proxy.generator.IdGenerator;
import qunar.tc.bistoury.proxy.web.dao.AppServerDao;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingBuilder;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.support.AppServer;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 14:21
 */
@Service
public class DefaultAgentInfoManager implements AgentInfoManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAgentInfoManager.class);

    private Map<String, String> agentConfig;

    @Autowired
    private IdGenerator generator;

    @Autowired
    private AgentConnectionStore agentConnectionStore;

    @Autowired
    private AgentInfoOverride agentInfoOverride;

    @Autowired
    private AppServerDao appServerDao;

    @PostConstruct
    public void init() {
        DynamicConfigLoader.load("agent_config.properties", false).addListener(conf -> agentConfig = conf.asMap());
    }

    @Override
    public ListenableFuture<Map<String, String>> getAgentInfo(String ip) {
        SettableFuture<Map<String, String>> resultFuture = SettableFuture.create();
        AppServer appServer = this.appServerDao.getAppServerByIp(ip);
        Map<String, String> agentInfo = new HashMap<>();
        if (appServer != null) {
            agentInfo.put("port", String.valueOf(appServer.getPort()));
            agentInfo.put("cpuJStackOn", String.valueOf(appServer.isAutoJStackEnable()));
            agentInfo.put("heapJMapHistoOn", String.valueOf(appServer.isAutoJMapHistoEnable()));
        }
        //这里可以覆盖所有版本配置
        agentInfo.putAll(agentConfig);

        final int version = getVersion(ip);
        //这里可以覆盖版本低于指定版本的配置
        agentInfoOverride.overrideAgentInfo(agentInfo, version);
        resultFuture.set(agentInfo);
        return resultFuture;
    }

    @Override
    public void updateAgentInfo(List<String> agentIds) {
        agentIds.forEach(agentId -> {
            Optional<AgentConnection> optionalAgentConnection = agentConnectionStore.getConnection(agentId);
            if (optionalAgentConnection.isPresent()) {
                logger.info("notify agent {} update meta info ", agentId);
                AgentConnection agentConnection = optionalAgentConnection.get();
                agentConnection.write(RemotingBuilder.buildRequestDatagram(CommandCode.REQ_TYPE_REFRESH_TIP.getCode(), generator.generateId(), null));
            }
        });
    }

    private int getVersion(String ip) {
        Optional<AgentConnection> connection = agentConnectionStore.getConnection(ip);
        if (connection.isPresent()) {
            return connection.get().getVersion();
        }
        return -1;
    }
}
