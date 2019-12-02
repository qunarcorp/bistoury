package qunar.tc.bistoury.attach.arthas.profiler;

import com.google.common.collect.Maps;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Description;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.cli.annotations.Option;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.arthas.util.TypeResponseResult;
import qunar.tc.bistoury.attach.common.BistouryLoggerHelper;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.*;
import qunar.tc.bistoury.instrument.client.profiler.AgentProfilerContext;

import java.util.HashMap;
import java.util.Map;

import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.*;


/**
 * @author cai.wen created on 2019/10/23 8:40
 */
@Name(BistouryConstants.REQ_PROFILER_START)
public class ProfilerStartCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private String profilerId;

    private long frequency;

    private final Map<String, String> config = Maps.newHashMapWithExpectedSize(2);

    @Option(shortName = "d", longName = "duration")
    public void setDuration(String duration) {
        config.put(DURATION, duration);
    }

    @Option(shortName = "f", longName = "frequency")
    public void setFrequency(String frequency) {
        this.frequency = Long.parseLong(frequency);
        config.put(FREQUENCY, frequency);
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
    public void setMode(String mode) {
        config.put(MODE, mode);
    }

    @Option(longName = "threads", flag = true)
    @Description("profile different threads separately")
    public void setThreads(boolean threads) {
        config.put(THREADS, String.valueOf(threads));
    }

    @Override
    public void process(CommandProcess process) {
        logger.info("receive profiler add command, mode: {}, config: {}", config);
        Map<String, String> result = new HashMap<>();
        result.put("profilerId", profilerId);
        TypeResponse typeResponse = TypeResponseResult.create(result, BistouryConstants.REQ_PROFILER_START);
        CodeProcessResponse response = typeResponse.getData();
        try {
            if (AgentProfilerContext.isProfiling()) {
                response.setMessage("target vm is profiling.");
                response.setCode(-1);
                return;
            }

            ProfilerClient profilerClient = ProfilerClients.getInstance();
            profilerClient.start(config);
            response.setCode(0);
            AgentProfilerContext.startProfiling(profilerId, frequency);
            result.put("state", Boolean.TRUE.toString());
            response.setMessage("add profiler success.");
        } catch (Exception e) {
            logger.error("", BistouryLoggerHelper.formatMessage("profiler add error. config: {}", config), e);
            response.setMessage("add profiler error. reason:" + e.getMessage());
        } finally {
            process.write(URLCoder.encode(JacksonSerializer.serialize(typeResponse)));
            process.end();
            config.clear();
        }
    }
}
