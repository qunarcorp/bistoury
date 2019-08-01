package qunar.tc.bistoury.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.netty.Processor;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/1/10 15:19
 */
public class MetaRefreshProcessor implements Processor<String> {

    private static final Logger logger = LoggerFactory.getLogger(MetaRefreshProcessor.class);

    private final MetaStore metaStore = MetaStores.getMetaStore();

    @Override
    public List<Integer> types() {
        return ImmutableList.of(CommandCode.REQ_TYPE_REFRESH_AGENT_INFO.getCode());
    }

    @Override
    public void process(RemotingHeader header, String command, ResponseHandler handler) {
        try {
            Map<String, String> agentInfo = JacksonSerializer.deSerialize(command, new TypeReference<Map<String, String>>() {
            });
            if (agentInfo != null && agentInfo.size() > 0) {
                logger.info("meta refresh data receive, {}", agentInfo);
                metaStore.update(agentInfo);
            }
        } catch (Exception e) {
            logger.error("update meta store error", e);
        }
    }
}
