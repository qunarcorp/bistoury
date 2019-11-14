package qunar.tc.bistoury.attach.arthas.profiler;

import com.google.common.collect.Maps;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Description;
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

import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.*;


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
        long value = Long.parseLong(duration);
        config.put(DURATION, value);
    }

    @Option(shortName = "f", longName = "frequency")
    public void setFrequency(String frequency) {
        long value = Long.parseLong(frequency);
        config.put(FREQUENCY, value);
    }

    @Option(shortName = "t", longName = "tmpdir")
    public void setTmpdir(String tmpdir) {
        config.put(TMP_DIR, URLCoder.decode(tmpdir));
    }

    @Option(shortName = "e", longName = "event")
    public void setEvent(String event) {
        config.put(EVENT, event);
    }

    @Argument(index = 0, argName = "id")
    public void setId(String id) {
        this.profilerId = id;
        config.put(PROFILER_ID, id);
    }

    @Option(shortName = "m", longName = "mode")
    public void setMode(int mode) {
        this.mode = Mode.codeOf(mode);
    }

    @Option(longName = "threads", flag = true)
    @Description("profile different threads separately")
    public void setThreads(boolean threads) {
        config.put(THREADS, threads);
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
            profilerClient.startProfiler(mode, config);
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
