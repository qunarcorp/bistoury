package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.processor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.UiResponses;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
import qunar.tc.bistoury.proxy.communicate.ui.linuxcommand.CommandSplitter;
import qunar.tc.bistoury.proxy.communicate.ui.linuxcommand.LinuxCommand;
import qunar.tc.bistoury.proxy.communicate.ui.linuxcommand.LinuxCommandParser;
import qunar.tc.bistoury.proxy.communicate.ui.linuxcommand.StandardCommand;
import qunar.tc.bistoury.remoting.command.MachineCommand;
import qunar.tc.bistoury.remoting.protocol.CommandCode;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/5/23 18:27
 */
@Service
public class LinuxCommandProcessor extends AbstractCommand<MachineCommand> {

    private static final Logger logger = LoggerFactory.getLogger(LinuxCommandProcessor.class);

    private static final Set<StandardCommand> singleMachineCommands = Sets.newHashSet(StandardCommand.cat, StandardCommand.tail);
    private static final String UNBUFFERED = "stdbuf -o0 ";

    @Override
    public Set<Integer> getCodes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_COMMAND.getCode());
    }

    @Override
    public int getMinAgentVersion() {
        return -1;
    }

    @Override
    public boolean supportMulti() {
        return true;
    }

    @Override
    protected Optional<RequestData<MachineCommand>> doPreprocessor(RequestData<MachineCommand> requestData, ChannelHandlerContext ctx) {
        try {
            // 验证用户权限
            logger.info("receive command {}", requestData);

            // 验证命令合法性
            String line = requestData.getCommand().getCommand();
            if (line.contains("`")) {
                throw new RuntimeException("命令不合法，非法字符。");
            }
            List<CommandSplitter.CommandPart> ss = CommandSplitter.split(line);
            String formattedCommand = "";
            boolean needUnbuffered = false;
            for (CommandSplitter.CommandPart part : ss) {
                if (part.getType() != CommandSplitter.PartType.command) {
                    formattedCommand += " " + part.getContent();
                    continue;
                }
                LinuxCommand linuxCommand = LinuxCommandParser.parse(part.getContent());
                String error = isSingleMachineCommand(requestData, linuxCommand);
                if (error != null) {
                    ctx.writeAndFlush(UiResponses.createProcessRequestErrorResponse(requestData, error));
                    return Optional.empty();
                }
                // tail -f 需要加上 stdbuf -i0 -o0
                if (linuxCommand.getStandardCommand() == StandardCommand.tail && linuxCommand.getCommandLine().hasOption("f")) {
                    formattedCommand += " " + part.getContent();
                    needUnbuffered = true;
                } else if (needUnbuffered) {
                    formattedCommand += " " + UNBUFFERED + part.getContent();
                } else {
                    formattedCommand += " " + part.getContent();
                }
            }

            MachineCommand command = new MachineCommand();
            command.setWorkDir(requestData.getAgentServerInfos().iterator().next().getLogdir());
            command.setCommand(formattedCommand);
            requestData.setCommand(command);
            return Optional.of(requestData);
        } catch (Exception e) {
            ctx.writeAndFlush(UiResponses.createProcessRequestErrorResponse(requestData, e.getMessage()));
            return Optional.empty();
        }
    }

    private String isSingleMachineCommand(RequestData requestData, LinuxCommand linuxCommand) {
        if ((singleMachineCommands.contains(linuxCommand.getStandardCommand())) && (requestData.getHosts() == null || requestData.getHosts().size() != 1)) {
            return linuxCommand.getStandardCommand().name() + " 必须选择一台机器执行";
        }
        return null;
    }
}
