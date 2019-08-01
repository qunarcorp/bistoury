package qunar.tc.bistoury.attach.arthas.debug;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.*;

/**
 * @author zhenyu.nie created on 2018 2018/11/28 20:05
 */
@Name(BistouryConstants.REQ_DEBUG_REMOVE)
public class QDebugRemoveCommand extends AnnotatedCommand {

    private static final Logger logger = BistouryLoggger.getLogger();

    private String id;

    @Argument(index = 0, argName = "id")
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void process(CommandProcess process) {
        logger.info("receive remove command, id [{}]", (Object) id);
        CodeProcessResponse<String> codeResponse = new CodeProcessResponse<>();
        TypeResponse<String> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_DEBUG_REMOVE);
        typeResponse.setData(codeResponse);
        try {
            QDebugClient debugClient = QDebugClients.getInstance();
            debugClient.remoteBreakPoint(id);
            codeResponse.setId(id);
            codeResponse.setCode(0);
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
