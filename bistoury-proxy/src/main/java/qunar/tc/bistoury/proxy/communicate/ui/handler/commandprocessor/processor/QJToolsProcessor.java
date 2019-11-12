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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.remoting.protocol.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
import qunar.tc.bistoury.remoting.command.MachineCommand;
import qunar.tc.bistoury.remoting.protocol.CommandCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author leix.xie
 * @date 2019/5/22 16:36
 * @describe
 */
@Service
public class QJToolsProcessor extends AbstractCommand<MachineCommand> {

    private static final Splitter SPACE_SPLITTER = Splitter.on(' ').omitEmptyStrings();
    private static final Joiner SPACE_JOINER = Joiner.on(' ');

    private static final String QJMXCLI = "qjmxcli";

    private Set<String> needSaveSet = ImmutableSet.of("qjdump");

    @Override
    protected Optional<RequestData<MachineCommand>> doPreprocessor(RequestData<MachineCommand> requestData, ChannelHandlerContext ctx) {
        return Optional.of(preProcessorCommand(requestData));
    }

    @Override
    protected MachineCommand prepareCommand(RequestData<MachineCommand> data, String agentId) {
        MachineCommand machineCommand = new MachineCommand();
        machineCommand.setCommand(data.getCommand().getCommand());
        if (needFindPid(machineCommand.getCommand())) {
            addPid(machineCommand, BistouryConstants.FILL_PID);
        } else {
            replacePidOption(machineCommand);
        }
        machineCommand.setWorkDir(data.getAgentServerInfos().iterator().next().getLogdir());
        return machineCommand;
    }


    private RequestData<MachineCommand> preProcessorCommand(RequestData<MachineCommand> data) {
        String commandStr = data.getCommand().getCommand();
        String commandName = firstArg(commandStr);
        //暂时只为qjdump
        if (needSaveSet.contains(commandName)) {
            data.setType(CommandCode.REQ_TYPE_COMMAND.getCode());
        }

        if (QJMXCLI.equals(commandName)) {
            String newCommandStr = processQJMXCommand(commandStr);
            data.getCommand().setCommand(newCommandStr);
        }
        return data;
    }

    private String processQJMXCommand(String commandStr) {
        List<String> inputList = SPACE_SPLITTER.splitToList(commandStr);
        if (inputList.isEmpty()) {
            throw new IllegalStateException("no command");
        } else if (inputList.size() == 1) {
            return inputList.get(0) + " -";
        } else {
            checkNotAddress(inputList.get(1));
        }

        int index = commandStr.indexOf(' ');
        if (index >= 0) {
            return commandStr.substring(0, index) + " -" + commandStr.substring(index);
        } else {
            return commandStr + " -";
        }
    }

    private boolean needFindPid(String command) {
        //是否制定pid
        List<String> options = Splitter.on(" ").omitEmptyStrings().splitToList(command);
        if (options.get(options.size() - 1).startsWith("pid:")) {
            return false;
        }
        //是否是帮助选项
        if (options.size() > 1) {
            String helpOption = options.get(1);
            if ("-h".equals(helpOption) || "--help".equals(helpOption)) {
                return false;
            }
        }

        return true;
    }

    private void replacePidOption(MachineCommand data) {
        data.setCommand(data.getCommand().replace("pid:", " "));
    }


    private void addPid(MachineCommand data, String pid) {
        String commandName = firstArg(data.getCommand());
        switch (commandName) {
            case "qjdump":
                String realCommand = fixQjdumpPid(data.getCommand(), pid);
                data.setCommand(realCommand);
                break;
            case "qjmap":
                doAddPid(data, pid, -1);
                break;
            case "qjmxcli":
                doAddPid(data, pid, 2);
                break;
            case "qjtop":
                doAddPid(data, pid, -1);
                break;
        }


    }

    /**
     * @param pidPosition 添加在最后面
     */
    private void doAddPid(MachineCommand data, String pid, int pidPosition) {
        if (pidPosition == -1) {
            data.setCommand(data.getCommand() + " " + pid);
            return;
        }
        List<String> args = Splitter.on(" ").omitEmptyStrings().splitToList(data.getCommand());
        List<String> newArgs = new ArrayList<>(args.size() + 1);
        int argsPosition = 0;
        for (int i = 0; i < args.size() + 1; i++) {
            if (pidPosition != i) {
                newArgs.add(args.get(argsPosition));
                argsPosition++;
            } else {
                newArgs.add(pid);
            }
        }

        data.setCommand(Joiner.on(" ").join(newArgs));
    }

    private static String fixQjdumpPid(String command, String pid) {
        List<String> commandTokens = Lists.newArrayList(SPACE_SPLITTER.split(command));
        commandTokens.add(pid);
        return SPACE_JOINER.join(commandTokens);
    }

    private void checkNotAddress(String input) {
        int index = input.indexOf(':');
        if (index > 0 && index < input.length() - 1) {
            for (int i = index + 1; i < input.length(); ++i) {
                if (input.charAt(i) < '0' || input.charAt(i) > '9') {
                    return;
                }
            }
            throw new IllegalStateException();
        }
    }

    private String firstArg(String command) {
        command = command.trim();
        if (Strings.isNullOrEmpty(command)) {
            return "";
        }
        int position = command.indexOf(' ');
        if (position == -1) {
            return command;
        }
        return command.substring(0, position);

    }

    @Override
    public Set<Integer> getCodes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_QJTOOLS.getCode());
    }

    @Override
    public int getMinAgentVersion() {
        return -1;
    }

    @Override
    public boolean supportMulti() {
        return false;
    }
}
