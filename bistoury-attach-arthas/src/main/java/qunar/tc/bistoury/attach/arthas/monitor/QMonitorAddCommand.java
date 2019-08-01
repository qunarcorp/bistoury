package qunar.tc.bistoury.attach.arthas.monitor;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.*;

/**
 * @author: leix.xie
 * @date: 2018/12/27 10:21
 * @describe：
 */
@Name(BistouryConstants.REQ_MONITOR_ADD)
public class QMonitorAddCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private String source;

    private int line;

    public String id;

    @Argument(index = 0, argName = "id")
    public void setId(String id) {
        this.id = id;
    }

    @Argument(index = 1, argName = "source")
    public void setSource(String source) {
        this.source = URLCoder.decode(source);
    }

    @Argument(index = 2, argName = "line")
    public void setLine(int line) {
        this.line = line;
    }


    @Override
    public void process(CommandProcess process) {
        logger.info("receive monitor add command, source: {}, line: {}", source, line);
        CodeProcessResponse<String> response = new CodeProcessResponse<>();
        TypeResponse<String> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_MONITOR_ADD);
        typeResponse.setData(response);
        response.setId(id);
        try {
            final QMonitorClient monitorClient = QMonitorClients.getInstance();
            String monitorId = monitorClient.addMonitor(source, line);
            response.setData(monitorId);
            response.setCode(0);
        } catch (Throwable e) {
            logger.error("qmonitor add error, {}", e.getMessage(), e);
            response.setCode(-1);
            response.setMessage("qmonitor add error: " + e.getMessage());
        } finally {
            process.write(URLCoder.encode(JacksonSerializer.serialize(typeResponse)));
            process.end();
        }
    }
}
