package qunar.tc.bistoury.attach.arthas.config;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.attach.file.bean.FileBean;
import qunar.tc.bistoury.common.*;

import java.util.List;

/**
 * @author: leix.xie
 * @date: 2019/3/5 10:28
 * @describe：
 */
@Name(BistouryConstants.REQ_APP_CONFIG)
public class AppConfigCommand extends AnnotatedCommand {
    private static final Logger logger = BistouryLoggger.getLogger();

    @Override
    public void process(CommandProcess process) {
        logger.info("receive app config command");
        CodeProcessResponse<List<FileBean>> response = new CodeProcessResponse<>();
        TypeResponse<List<FileBean>> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_APP_CONFIG);
        typeResponse.setData(response);
        try {
            AppConfigClient client = AppConfigClients.getInstance();
            List<FileBean> config = getAppConfig(client);
            response.setCode(0);
            response.setData(config);
        } catch (Exception e) {
            logger.error("get app config error, {}", e.getMessage(), e);
            response.setCode(-1);
            response.setMessage("获取配置文件信息出错, " + e.getMessage());
        } finally {
            process.write(URLCoder.encode(JacksonSerializer.serialize(typeResponse)));
            process.end();
        }
    }

    private List<FileBean> getAppConfig(final AppConfigClient client) {
        List<FileBean> files = client.listAppConfigFiles();
        return files;
    }
}
