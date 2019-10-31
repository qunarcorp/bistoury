package qunar.tc.bistoury.attach.arthas.profiler;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.*;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.sampling.Manager;

/**
 * @author cai.wen created on 2019/10/25 11:12
 */
@Name(BistouryConstants.REQ_PROFILER_STOP)
public class ProfilerStopCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private String id;

    @Argument(index = 0, argName = "id")
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void process(CommandProcess process) {
        logger.info("receive profiler stop command. id: {}", id);
        CodeProcessResponse<String> response = new CodeProcessResponse<>();
        TypeResponse<String> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_PROFILER_STOP);
        typeResponse.setData(response);


        try {
            if (!AgentProfilerContext.isProfiling()) {
                response.setMessage("target vm is already stop.");
                response.setCode(-1);
                return;
            }

            Manager.stop();
            response.setCode(0);
            response.setData("stop profiler success.");
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("stop profiler error. reason: " + e.getMessage());
            logger.error("", "profiler stop error.");
        } finally {
            process.write(URLCoder.encode(JacksonSerializer.serialize(typeResponse)));
            process.end();
        }

    }
}
