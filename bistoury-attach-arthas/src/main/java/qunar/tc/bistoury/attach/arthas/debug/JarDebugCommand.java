package qunar.tc.bistoury.attach.arthas.debug;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.*;

import java.util.Set;

/**
 * @author: leix.xie
 * @date: 2019/2/27 17:30
 * @describe：
 */
@Name(BistouryConstants.REQ_JAR_DEBUG)
public class JarDebugCommand extends AnnotatedCommand {
    private static final Logger logger = BistouryLoggger.getLogger();

    @Override
    public void process(CommandProcess process) {
        logger.info("receive jar debug command");
        CodeProcessResponse<Set<String>> codeResponse = new CodeProcessResponse<>();
        TypeResponse<Set<String>> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_JAR_DEBUG);
        typeResponse.setData(codeResponse);
        try {
            JarDebugClient client = JarDebugClients.getInstance();
            Set<String> classPaths = client.getAllClass();
            codeResponse.setCode(0);
            codeResponse.setData(classPaths);
        } catch (Exception e) {
            logger.error("", "get jar debug info error", e);
            codeResponse.setCode(-1);
            codeResponse.setMessage("获取类列表失败，" + e.getMessage());
        } finally {
            process.write(URLCoder.encode(JacksonSerializer.serialize(typeResponse)));
            process.end();
        }
    }
}
