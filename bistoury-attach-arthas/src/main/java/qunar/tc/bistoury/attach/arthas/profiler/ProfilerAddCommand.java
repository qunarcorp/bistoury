package qunar.tc.bistoury.attach.arthas.profiler;

import com.google.common.collect.Maps;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.cli.annotations.Option;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.CodeProcessResponse;
import qunar.tc.bistoury.common.TypeResponse;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.Mode;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants;

import java.util.Map;


/**
 * @author cai.wen created on 2019/10/23 8:40
 */
@Name(BistouryConstants.REQ_PROFILER_ADD)
public class ProfilerAddCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private Mode mode;

    private final Map<String, Object> config = Maps.newHashMapWithExpectedSize(2);

    @Option(shortName = "d", longName = "duration")
    public void setDuration(String duration) {
        int value = Integer.parseInt(duration);
        config.put(ProfilerConstants.DURATION, value);
    }

    @Option(shortName = "f", longName = "frequency")
    public void setFrequency(String frequency) {
        int value = Integer.parseInt(frequency);
        config.put(ProfilerConstants.FREQUENCY, value);
    }

    @Option(shortName = "m", longName = "mode")
    public void setMode(int mode) {
        this.mode = Mode.codeOf(mode);
    }

    @Override
    public void process(CommandProcess process) {
        logger.info("receive profiler add command, mode: {}, config: {}", mode, config);
        CodeProcessResponse<String> response = new CodeProcessResponse<>();
        TypeResponse<String> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_PROFILER_ADD);
        typeResponse.setData(response);

        if (AgentProfilerContext.isProfiling()) {
            process.write("target vm is profiling.");
            process.end();
            return;
        }

        try {
            ProfilerClient profilerClient = ProfilerClients.getInstance();
            profilerClient.startProfiling(mode, config);
            response.setCode(0);
            process.write("add profiler success.");
        } catch (Exception e) {
            logger.error("profiler add error. mode: {}, config: {}", mode.toString(), config, e);
            process.write("add profiler error. reason:" + e.getMessage());
        } finally {
            process.end();
            config.clear();
        }
    }
}
