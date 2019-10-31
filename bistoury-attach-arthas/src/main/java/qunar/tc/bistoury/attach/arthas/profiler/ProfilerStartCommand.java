package qunar.tc.bistoury.attach.arthas.profiler;

import com.google.common.collect.Maps;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.cli.annotations.Option;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.*;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.Mode;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants;

import java.util.Map;


/**
 * @author cai.wen created on 2019/10/23 8:40
 */
@Name(BistouryConstants.REQ_PROFILER_START)
public class ProfilerStartCommand extends AnnotatedCommand {

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

    @Option(shortName = "t", longName = "tmpdir")
    public void setTmpdir(String tmpdir) {
        config.put(ProfilerConstants.TMP_DIR, URLCoder.decode(tmpdir));
    }

    @Argument(index = 0, argName = "id")
    public void setId(String id) {
        config.put(ProfilerConstants.PROFILER_ID, id);
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
        typeResponse.setType(BistouryConstants.REQ_PROFILER_START);
        typeResponse.setData(response);
        response.setId((String) config.get(ProfilerConstants.PROFILER_ID));

        try {
            if (AgentProfilerContext.isProfiling()) {
                response.setMessage("target vm is profiling.");
                response.setCode(-1);
                return;
            }

            ProfilerClient profilerClient = ProfilerClients.getInstance();
            profilerClient.startProfiling(mode, config);
            response.setCode(0);
            response.setData("add profiler success.");
        } catch (Exception e) {
            logger.error("profiler add error. mode: {}, config: {}", mode.toString(), config, e);
            response.setMessage("add profiler error. reason:" + e.getMessage());
        } finally {
            process.write(URLCoder.encode(JacksonSerializer.serialize(typeResponse)));
            process.end();
            config.clear();
        }
    }
}
