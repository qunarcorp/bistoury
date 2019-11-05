package qunar.tc.bistoury.attach.arthas.profiler;

import com.google.common.collect.Maps;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.cli.annotations.Option;
import qunar.tc.bistoury.attach.arthas.util.TypeResponseResult;
import qunar.tc.bistoury.attach.common.BistouryLoggerHelper;
import qunar.tc.bistoury.common.*;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.Mode;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants;

import java.util.HashMap;
import java.util.Map;


/**
 * @author cai.wen created on 2019/10/23 8:40
 */
@Name(BistouryConstants.REQ_PROFILER_START)
public class ProfilerStartCommand extends AnnotatedCommand {

    private Mode mode;

    private String profilerId;

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
        this.profilerId = id;
        config.put(ProfilerConstants.PROFILER_ID, id);
    }

    @Option(shortName = "m", longName = "mode")
    public void setMode(int mode) {
        this.mode = Mode.codeOf(mode);
    }

    @Override
    public void process(CommandProcess process) {
        BistouryLoggerHelper.info("receive profiler add command, mode: {}, config: {}", mode, config);
        Map<String, Object> result = new HashMap<>();
        TypeResponse typeResponse = TypeResponseResult.create(result, BistouryConstants.REQ_PROFILER_START);
        CodeProcessResponse response = typeResponse.getData();
        try {
            if (AgentProfilerContext.isProfiling()) {
                response.setMessage("target vm is profiling.");
                response.setCode(-1);
                return;
            }

            ProfilerClient profilerClient = ProfilerClients.getInstance();
            profilerClient.startProfiling(mode, config);
            response.setCode(0);
            result.put("profilerId", profilerId);
            response.setMessage("add profiler success.");
        } catch (Exception e) {
            BistouryLoggerHelper.error(e, "profiler add error. mode: {}, config: {}", mode, config);
            response.setMessage("add profiler error. reason:" + e.getMessage());
        } finally {
            process.write(URLCoder.encode(JacksonSerializer.serialize(typeResponse)));
            process.end();
            config.clear();
        }
    }
}
