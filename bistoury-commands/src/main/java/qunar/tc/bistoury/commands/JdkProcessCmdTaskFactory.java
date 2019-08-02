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

package qunar.tc.bistoury.commands;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.command.MachineCommand;
import qunar.tc.bistoury.remoting.netty.Task;
import qunar.tc.bistoury.remoting.netty.TaskFactory;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.Set;

public class JdkProcessCmdTaskFactory implements TaskFactory<MachineCommand> {

    private static final Logger logger = LoggerFactory.getLogger(JdkProcessCmdTaskFactory.class);

    private static final String NAME = "jdkProcessCommand";
    private static final Set<Integer> types;

    static {
        types = ImmutableSet.of(
                CommandCode.REQ_TYPE_COMMAND.getCode(),
                CommandCode.REQ_TYPE_JAVA.getCode(),
                CommandCode.REQ_TYPE_QJTOOLS.getCode());
    }

    @Override
    public Set<Integer> codes() {
        return types;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Task create(RemotingHeader header, MachineCommand command, ResponseHandler handler) {
        return new SystemTask(header.getId(), command.getCommand(), command.getWorkDir(), handler, header.getMaxRunningMs());
    }
}

    