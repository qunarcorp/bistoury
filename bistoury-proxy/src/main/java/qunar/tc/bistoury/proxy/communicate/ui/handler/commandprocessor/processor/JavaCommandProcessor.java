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
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
import qunar.tc.bistoury.remoting.command.MachineCommand;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

/**
 * @author leix.xie
 * @date 2019/5/22 16:25
 * @describe
 */
@Service
public class JavaCommandProcessor extends AbstractCommand<MachineCommand> {

    private static final String LOCATION = ".location";
    private static final String JSTACK = "jstack";

    private Map<String, String> globalConfig;

    @PostConstruct
    public void init() {
        DynamicConfigLoader.<LocalDynamicConfig>load("global.properties")
                .addListener(conf -> globalConfig = conf.asMap());
    }

    @Override
    protected MachineCommand prepareCommand(RequestData<MachineCommand> data, String agentId) {
        String command = data.getCommand().getCommand();
        final String commandLocation = globalConfig.get(command + LOCATION);
        final String newCommand;
        if (JSTACK.equals(command)) {
            newCommand = commandLocation + " " + BistouryConstants.FILL_PID;
        } else {
            newCommand = commandLocation + " -gcutil " + BistouryConstants.FILL_PID + " 1000 1000";
        }
        MachineCommand machineCommand = new MachineCommand();
        machineCommand.setCommand(newCommand);
        machineCommand.setWorkDir(data.getAgentServerInfos().iterator().next().getLogdir());
        return machineCommand;
    }

    @Override
    public Set<Integer> getCodes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_JAVA.getCode());
    }

    @Override
    public int getMinAgentVersion() {
        return -1;
    }

    @Override
    public boolean supportMulti() {
        return true;
    }
}
