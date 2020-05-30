package qunar.tc.bistoury.attach.arthas.profiler;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
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

/**
 * @author cai.wen created on 2019/10/25 11:12
 */
@Name(BistouryConstants.REQ_PROFILER_STOP)
public class ProfilerStopCommand extends AnnotatedCommand {

    private static final Logger LOGGER = BistouryLoggger.getLogger();

    private String id;

    private boolean isForceStop = false;

    @Argument(index = 0, argName = "id", required = false)
    public void setId(String id) {
        this.id = id;
    }

    //强制关闭不需要输入id,只能在命令行输入 profilerstop -f
    @Option(shortName = "f", longName = "force", flag = true)
    public void setForceStop(boolean isForceStop) {
        this.isForceStop = isForceStop;
    }

    @Override
    public void process(CommandProcess process) {
        LOGGER.info("", "receive profiler stop command. id: {}", id);
        Map<String, String> result = new HashMap<>();
        result.put("profilerId", id);
        TypeResponse typeResponse = TypeResponseResult.create(result, BistouryConstants.REQ_PROFILER_STOP);
        CodeProcessResponse response = typeResponse.getData();
        try {
            GProfilerClient client = GProfilerClients.getInstance();

            if (isForceStop) {
                LOGGER.warn("", "receive profiler force stop command.");
                result.put("profilerId", "");
                client.clear();
                response.setCode(0);
                response.setMessage("force stop success.");
                result.put("forceStop", Boolean.TRUE.toString());
                result.put("state", Boolean.TRUE.toString());
                return;
            }

            if (!client.isRunning()) {
                response.setMessage("target vm is already stop.");
                response.setCode(-1);
                result.put("state", Boolean.TRUE.toString());
                return;
            }

            client.stop(id);
            result.put("state", Boolean.TRUE.toString());
            response.setCode(0);
            response.setMessage("stop profiler success.");
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("stop profiler error. reason: " + e.getMessage());
            LOGGER.error("", BistouryLoggerHelper.formatMessage("stop profiler error. id: {}", id), e);
        } finally {
            process.write(URLCoder.encode(AttachJacksonSerializer.serialize(typeResponse)));
            process.end();
        }
    }
}
