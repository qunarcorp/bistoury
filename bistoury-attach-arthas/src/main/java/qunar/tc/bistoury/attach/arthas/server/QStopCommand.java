package qunar.tc.bistoury.attach.arthas.server;

import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.cli.annotations.Summary;

/**
 * @author leix.xie
 * @date 2019/9/23 19:31
 * @describe
 */
@Name("stop")
@Summary("Stop/Shutdown Arthas server and exit the console. Alias for shutdown.")
public class QStopCommand extends AnnotatedCommand {
    @Override
    public void process(CommandProcess process) {
        QShutdownCommand.shutdown(process);
    }
}