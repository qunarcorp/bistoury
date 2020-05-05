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
import qunar.tc.bistoury.attach.common.AttachJacksonSerializer;
import qunar.tc.bistoury.attach.common.BistouryLoggerHelper;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.CodeProcessResponse;
import qunar.tc.bistoury.common.TypeResponse;
import qunar.tc.bistoury.common.URLCoder;

import java.util.HashMap;
import java.util.Map;

import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.DURATION;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.EVENT;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.INTERVAL;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.MODE;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.PROFILER_ID;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.STORE_DIR;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.THREADS;


/**
 * @author cai.wen created on 2019/10/23 8:40
 */
@Name(BistouryConstants.REQ_PROFILER_START)
public class ProfilerStartCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private final Map<String, String> config = Maps.newHashMapWithExpectedSize(2);

    @Option(shortName = "d", longName = "duration")
    public void setDuration(String duration) {
        config.put(DURATION, duration);
    }

    @Option(shortName = "i", longName = "interval")
    public void setInterval(String interval) {
        config.put(INTERVAL, interval);
    }

    @Option(shortName = "e", longName = "event")
    public void setEvent(String event) {
        config.put(EVENT, event);
    }

    @Argument(index = 0, argName = "id")
    public void setId(String id) {
        config.put(PROFILER_ID, id);
    }

    @Option(shortName = "m", longName = "mode")
    public void setMode(String mode) {
        config.put(MODE, mode);
    }

    @Option(shortName = "s", longName = "storeDir")
    public void setStoreDir(String storeDir) {
        config.put(STORE_DIR, URLCoder.decode(storeDir));
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
        result.put("profilerId", config.get(PROFILER_ID));
        TypeResponse typeResponse = TypeResponseResult.create(result, BistouryConstants.REQ_PROFILER_START);
        CodeProcessResponse response = typeResponse.getData();
        try {
            GProfilerClient profilerClient = GProfilerClients.getInstance();
            if (profilerClient.isRunning()) {
                response.setMessage("target vm is profiling.");
                response.setCode(-1);
                return;
            }

            profilerClient.start(config);
            response.setCode(0);
            result.put("state", Boolean.TRUE.toString());
            response.setMessage("add profiler success.");
        } catch (Exception e) {
            logger.error("", BistouryLoggerHelper.formatMessage("profiler add error. config: {}", config), e);
            response.setCode(-1);
            response.setMessage("add profiler error. reason:" + e.getMessage());
        } finally {
            process.write(URLCoder.encode(AttachJacksonSerializer.serialize(typeResponse)));
            process.end();
            config.clear();
        }
    }
}
