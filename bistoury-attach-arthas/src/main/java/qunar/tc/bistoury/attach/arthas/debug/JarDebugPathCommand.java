package qunar.tc.bistoury.attach.arthas.debug;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Argument;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.common.*;

/**
 * @author: leix.xie
 * @date: 2019/3/6 17:48
 * @describe：
 */
@Name(BistouryConstants.REQ_JAR_CLASS_PATH)
public class JarDebugPathCommand extends AnnotatedCommand {
    private static final Logger logger = BistouryLoggger.getLogger();

    private String className;

    @Argument(index = 0, argName = "className")
    public void setClassName(final String className) {
        this.className = URLCoder.decode(className);
    }

    @Override
    public void process(CommandProcess process) {
        logger.info("receive jar class path command, className: " + className);
        CodeProcessResponse<ClassInfo> codeResponse = new CodeProcessResponse<>();
        TypeResponse<ClassInfo> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_JAR_CLASS_PATH);
        typeResponse.setData(codeResponse);
        try {
            JarDebugClient client = JarDebugClients.getInstance();
            ClassInfo classInfo = client.getClassPath(className);

            codeResponse.setCode(0);
            codeResponse.setId(className);
            codeResponse.setData(classInfo);
        } catch (Exception e) {
            logger.error("", "get jar class path error, className: " + className, e);
            codeResponse.setCode(-1);
            codeResponse.setMessage("获取类路径失败，" + e.getMessage());
        } finally {
            process.write(URLCoder.encode(JacksonSerializer.serialize(typeResponse)));
            process.end();
        }
    }
}
