package qunar.tc.bistoury.attach.arthas.profiler;

import com.google.common.base.Optional;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.logger.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import qunar.tc.bistoury.attach.arthas.util.TypeResponseResult;
import qunar.tc.bistoury.attach.common.AttachJacksonSerializer;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.TypeResponse;
import qunar.tc.bistoury.common.URLCoder;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cai.wen created on 19-12-30 上午11:36
 */
@Name(BistouryConstants.REQ_PROFILER_INFO)
public class ProfilerInfoCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM-dd-yyyy HH:mm:ss");

    @Override
    public void process(CommandProcess process) {
        logger.info("", "receive profiler info command.");
        Map<String, String> result = new HashMap<>();
        TypeResponse typeResponse = TypeResponseResult.create(result, BistouryConstants.REQ_PROFILER_INFO);
        try {
            GProfilerClient client = GProfilerClients.getInstance();
            Optional<ProfilerContext> currentProfilingOptional = client.getCurrentProfiling();
            if (currentProfilingOptional.isPresent()) {
                ProfilerContext profilerContext = currentProfilingOptional.get();
                result.put("isProfiling", "true");
                result.put("startTime", dateTimeFormatter.print(profilerContext.getStartTime()));
                result.put("id", profilerContext.getId());
                result.put("interval", String.valueOf(profilerContext.getIntervalMs()));
            } else {
                result.put("isProfiling", "false");
                result.put("startTime", dateTimeFormatter.print(0));
                result.put("id", "");
                result.put("interval", String.valueOf(0));
            }

            typeResponse.getData().setCode(0);
        } finally {
            process.write(URLCoder.encode(AttachJacksonSerializer.serialize(typeResponse)));
            process.end();
        }
    }
}
