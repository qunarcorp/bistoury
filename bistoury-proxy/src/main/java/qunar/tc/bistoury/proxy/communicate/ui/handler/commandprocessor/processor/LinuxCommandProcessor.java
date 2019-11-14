/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.processor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.application.api.AppService;
import qunar.tc.bistoury.proxy.communicate.ui.UiResponses;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
import qunar.tc.bistoury.proxy.communicate.ui.linuxcommand.CommandSplitter;
import qunar.tc.bistoury.proxy.communicate.ui.linuxcommand.LinuxCommand;
import qunar.tc.bistoury.proxy.communicate.ui.linuxcommand.LinuxCommandParser;
import qunar.tc.bistoury.proxy.communicate.ui.linuxcommand.StandardCommand;
import qunar.tc.bistoury.proxy.util.ChannelUtils;
import qunar.tc.bistoury.remoting.command.MachineCommand;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RequestData;
import qunar.tc.bistoury.serverside.exception.PermissionDenyException;

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
    //private static final String UNBUFFERED = "stdbuf -o0 ";

    @Autowired
    private AppService appService;

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
    protected Optional<RequestData<MachineCommand>> doPreprocessor(RequestData<MachineCommand> requestData, ChannelHandlerContext ctx) throws Exception {
        String ip = ChannelUtils.getIp(ctx.channel());
        String appCode = requestData.getApp();
        String userCode = requestData.getUser();

        logger.info("receive from {}[{}] command: {}", ip, userCode, requestData);
        // 验证用户权限
        if (!this.appService.checkUserPermission(appCode, userCode)) {
            logger.warn("{}[{}] want to visit {}[{}] with no permission", ip, userCode, appCode, requestData.getHosts());
            throw new PermissionDenyException("Permission Deny, you're not in owner of app " + appCode);
        }

        // 验证命令合法性
        String line = requestData.getCommand().getCommand();
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
                //formattedCommand += " " + UNBUFFERED + part.getContent();
                throw new RuntimeException("The command [tail -f filename] does not support pipes");
            } else {
                formattedCommand += " " + part.getContent();
            }
        }

        MachineCommand command = new MachineCommand();
        command.setWorkDir(requestData.getAgentServerInfos().iterator().next().getLogdir());
        command.setCommand(formattedCommand);
        requestData.setCommand(command);
        return Optional.of(requestData);
    }

    private String isSingleMachineCommand(RequestData requestData, LinuxCommand linuxCommand) {
        if ((singleMachineCommands.contains(linuxCommand.getStandardCommand())) && (requestData.getHosts() == null || requestData.getHosts().size() != 1)) {
            return linuxCommand.getStandardCommand().name() + " must select a machine to execution";
        }
        return null;
    }
}
