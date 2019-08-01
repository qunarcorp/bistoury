package qunar.tc.bistoury.attach.arthas.debug;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.cli.annotations.Option;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.*;

/**
 * @author zhenyu.nie created on 2018 2018/11/22 17:16
 */
@Name(BistouryConstants.REQ_DEBUG_ADD)
public class QDebugAddCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private String id;

    private String source;

    private int line;

    private String condition;

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

    @Option(shortName = "c", longName = "condition")
    public void setCondition(String condition) {
        this.condition = URLCoder.decode(condition);
    }

    @Override
    public void process(CommandProcess process) {
        logger.info("receive debug command, source [{}], line [{}], condition [{}], id [{}]", (Object) source, line, condition, id);
        CodeProcessResponse<String> codeResponse = new CodeProcessResponse<>();
        TypeResponse<String> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_DEBUG_ADD);
        typeResponse.setData(codeResponse);
        try {
            QDebugClient debugClient = QDebugClients.getInstance();
            String breakPointId = debugClient.registerBreakpoint(source, line, condition);
            codeResponse.setId(id);
            codeResponse.setCode(0);
            codeResponse.setData(breakPointId);
        } catch (Throwable e) {
            logger.error("qdebug-add-error", e.getMessage(), e);
            codeResponse.setId(id);
            codeResponse.setCode(-1);
            codeResponse.setMessage(e.getMessage());
        }
        process.write(URLCoder.encode(JacksonSerializer.serialize(typeResponse)));
        process.end();
    }
}
