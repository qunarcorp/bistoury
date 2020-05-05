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

package qunar.tc.bistoury.attach.arthas.server;

import com.taobao.arthas.core.command.BuiltinCommandPack;
import com.taobao.arthas.core.command.basic1000.*;
import com.taobao.arthas.core.command.hidden.JulyCommand;
import com.taobao.arthas.core.command.hidden.OptionsCommand;
import com.taobao.arthas.core.command.hidden.ThanksCommand;
import com.taobao.arthas.core.command.klass100.*;
import com.taobao.arthas.core.command.logger.LoggerCommand;
import com.taobao.arthas.core.command.monitor200.*;
import com.taobao.arthas.core.shell.command.Command;
import qunar.tc.bistoury.attach.arthas.agentInfo.AgentInfoCommand;
import qunar.tc.bistoury.attach.arthas.config.AppConfigCommand;
import qunar.tc.bistoury.attach.arthas.config.AppConfigFileCommand;
import qunar.tc.bistoury.attach.arthas.debug.*;
import qunar.tc.bistoury.attach.arthas.jar.JarInfoCommand;
import qunar.tc.bistoury.attach.arthas.monitor.QMonitorAddCommand;
import qunar.tc.bistoury.attach.arthas.monitor.QMonitorSnapshotCommand;
import qunar.tc.bistoury.attach.arthas.profiler.ProfilerInfoCommand;
import qunar.tc.bistoury.attach.arthas.profiler.ProfilerStateSearchCommand;
import qunar.tc.bistoury.attach.arthas.profiler.ProfilerStartCommand;
import qunar.tc.bistoury.attach.arthas.profiler.ProfilerStopCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhenyu.nie created on 2018 2018/11/22 16:39
 */
public class QBuiltinCommandPack extends BuiltinCommandPack {

    private static List<Command> commands = new ArrayList<Command>();

    static {
        initCommands();
    }

    @Override
    public List<Command> commands() {
        return commands;
    }

    private static void initCommands() {
        commands.add(Command.create(HelpCommand.class));
        commands.add(Command.create(KeymapCommand.class));
        commands.add(Command.create(SearchClassCommand.class));
        commands.add(Command.create(SearchMethodCommand.class));
        commands.add(Command.create(ClassLoaderCommand.class));
        commands.add(Command.create(JadCommand.class));
        commands.add(Command.create(GetStaticCommand.class));
        commands.add(Command.create(MonitorCommand.class));
        commands.add(Command.create(StackCommand.class));
        commands.add(Command.create(ThreadCommand.class));
        commands.add(Command.create(TraceCommand.class));
        commands.add(Command.create(WatchCommand.class));
        commands.add(Command.create(TimeTunnelCommand.class));
        commands.add(Command.create(JvmCommand.class));
        commands.add(Command.create(DashboardCommand.class));
        commands.add(Command.create(DumpClassCommand.class));
        commands.add(Command.create(JulyCommand.class));
        commands.add(Command.create(ThanksCommand.class));
        commands.add(Command.create(OptionsCommand.class));
        commands.add(Command.create(ClsCommand.class));
        commands.add(Command.create(ResetCommand.class));
        commands.add(Command.create(VersionCommand.class));
        commands.add(Command.create(SessionCommand.class));
        commands.add(Command.create(SystemPropertyCommand.class));
        commands.add(Command.create(RedefineCommand.class));
        //3.0.5
        commands.add(Command.create(SystemEnvCommand.class));
        commands.add(Command.create(HistoryCommand.class));
        commands.add(Command.create(OgnlCommand.class));
        //3.1.0
        commands.add(Command.create(PwdCommand.class));
        commands.add(Command.create(MemoryCompilerCommand.class));
        //3.1.1
        commands.add(Command.create(MBeanCommand.class));
        //3.1.2
        commands.add(Command.create(HeapDumpCommand.class));
        commands.add(Command.create(VMOptionCommand.class));
        commands.add(Command.create(LoggerCommand.class));
        commands.add(Command.create(QStopCommand.class));

        // qunar command
        commands.add(Command.create(AgentInfoCommand.class));

        commands.add(Command.create(QDebugAddCommand.class));
        commands.add(Command.create(QDebugRemoveCommand.class));
        commands.add(Command.create(QDebugSearchCommand.class));
        commands.add(Command.create(QDebugReleaseInfoCommand.class));
        commands.add(Command.create(QShutdownCommand.class));

        commands.add(Command.create(QMonitorAddCommand.class));
        commands.add(Command.create(QMonitorSnapshotCommand.class));

        commands.add(Command.create(JarInfoCommand.class));

        commands.add(Command.create(JarDebugCommand.class));
        commands.add(Command.create(JarDebugPathCommand.class));

        commands.add(Command.create(AppConfigCommand.class));
        commands.add(Command.create(AppConfigFileCommand.class));

        commands.add(Command.create(ProfilerStartCommand.class));
        commands.add(Command.create(ProfilerStopCommand.class));
        commands.add(Command.create(ProfilerStateSearchCommand.class));
        commands.add(Command.create(ProfilerInfoCommand.class));
    }
}
