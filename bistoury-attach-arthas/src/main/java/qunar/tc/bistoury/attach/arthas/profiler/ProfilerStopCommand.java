package qunar.tc.bistoury.attach.arthas.profiler;

import com.google.common.base.Strings;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Name;
import qunar.tc.bistoury.attach.arthas.util.TypeResponseResult;
import qunar.tc.bistoury.attach.common.BistouryLoggerHelper;
import qunar.tc.bistoury.common.*;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cai.wen created on 2019/10/25 11:12
 */
@Name(BistouryConstants.REQ_PROFILER_STOP)
public class ProfilerStopCommand extends AnnotatedCommand {

    private String id;

    @Argument(index = 0, argName = "id")
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void process(CommandProcess process) {
        BistouryLoggerHelper.info("receive profiler stop command. id: {}", id);
        Map<String, Object> result = new HashMap<>();
        TypeResponse typeResponse = TypeResponseResult.create(result, BistouryConstants.REQ_PROFILER_STOP);
        CodeProcessResponse response = typeResponse.getData();

        try {
            if (!AgentProfilerContext.isProfiling()) {
                response.setMessage("target vm is already stop.");
                response.setCode(-1);
                result.put("state", true);
                return;
            }

            final String curProfilerId = AgentProfilerContext.getProfilerId();
            if (Strings.isNullOrEmpty(id) || !id.equals(curProfilerId)) {
                response.setMessage("error profiler id.");
                response.setCode(-1);
                result.put("state", true);
                return;
            }

            ProfilerClients.getInstance().stopProfiler();
            result.put("profilerId", id);
            result.put("state", true);
            response.setCode(0);
            response.setMessage("stop profiler success.");
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("stop profiler error. reason: " + e.getMessage());
            BistouryLoggerHelper.error(e, "stop profiler error. id: {}", id);
        } finally {
            process.write(URLCoder.encode(JacksonSerializer.serialize(typeResponse)));
            process.end();
        }
    }
}
