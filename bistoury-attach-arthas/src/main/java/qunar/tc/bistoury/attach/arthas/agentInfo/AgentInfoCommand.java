package qunar.tc.bistoury.attach.arthas.agentInfo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.Util.AgentConfig;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.common.URLCoder;

import java.util.Map;

import static qunar.tc.bistoury.common.BistouryConstants.REQ_AGENT_INFO;

/**
 * @author: leix.xie
 * @date: 2019/2/18 14:56
 * @describe：
 */
@Name(REQ_AGENT_INFO)
public class AgentInfoCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final AgentConfig config = new AgentConfig(MetaStores.getMetaStore());

    private String agentInfo;

    @Argument(index = 0, argName = "agentInfo")
    public void setAgentInfo(final String agentInfo) {
        this.agentInfo = URLCoder.decode(agentInfo);
    }

    @Override
    public void process(CommandProcess process) {
        try {

            Map<String, String> info = JacksonSerializer.deSerialize(this.agentInfo, new TypeReference<Map<String, String>>() {
            });
            logger.debug("receive agent info update: {}", info);
            if (config.update(info)) {
                logger.info("update agent info: {}", info);
            }
        } catch (Throwable e) {
            logger.error("-1", "update meta info error", e);
        } finally {
            process.end();
        }
    }
}
