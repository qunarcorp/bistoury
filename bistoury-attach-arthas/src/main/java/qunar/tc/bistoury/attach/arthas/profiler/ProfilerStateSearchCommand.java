package qunar.tc.bistoury.attach.arthas.profiler;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.*;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;

/**
 * @author cai.wen created on 2019/10/25 11:17
 */
@Name(BistouryConstants.REQ_PROFILER_STATE_SEARCH)
public class ProfilerStateSearchCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private String id;

    @Argument(index = 0, argName = "id")
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void process(CommandProcess process) {
        logger.info("receive profiler search command, id: {}", id);
        CodeProcessResponse<String> response = new CodeProcessResponse<>();
        TypeResponse<String> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_PROFILER_STATE_SEARCH);
        typeResponse.setData(response);

        try {
            if (AgentProfilerContext.isProfiling()) {
                response.setData("profiler is stop.");
                response.setCode(0);
            } else {
                response.setMessage("profiler is profiling.");
                response.setCode(-1);
            }
        } finally {
            process.write(URLCoder.encode(JacksonSerializer.serialize(typeResponse)));
            process.end();
        }
    }
}
