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

package qunar.tc.bistoury.commands.arthas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.commands.arthas.telnet.NormalTelnetStore;
import qunar.tc.bistoury.commands.arthas.telnet.TelnetStore;
import qunar.tc.bistoury.commands.arthas.telnet.UrlEncodedTelnetStore;
import qunar.tc.bistoury.common.URLCoder;
import qunar.tc.bistoury.remoting.netty.Task;
import qunar.tc.bistoury.remoting.netty.TaskFactory;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.Map;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/5/28 16:47
 */
public class ArthasTaskFactory implements TaskFactory<String> {

    private static final String PID_SYMBOL = " -pid";

    private static final String ASYNC_COMMAND_SYMBOL = "&";

    private static final TelnetStore arthasTelnetStore = NormalTelnetStore.getInstance();

    private static final TelnetStore debugTelnetStore = UrlEncodedTelnetStore.getInstance();

    private static final Map<Integer, TelnetStore> storeMapping;

    static {
        storeMapping = ImmutableMap.<Integer, TelnetStore>builder()
                .put(CommandCode.REQ_TYPE_ARTHAS.getCode(), arthasTelnetStore)
                .put(CommandCode.REQ_TYPE_DEBUG.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_JAR_DEBUG.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_MONITOR.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_JAR_INFO.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_CONFIG.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_PROFILER_START.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_PROFILER_STOP.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_PROFILER_STATE_SEARCH.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_PROFILER_INFO.getCode(), debugTelnetStore)
                .build();
    }

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.copyOf(storeMapping.keySet());
    }

    @Override
    public String name() {
        return "arthas";
    }

    @Override
    public Task create(RemotingHeader header, String command, ResponseHandler handler) {
        int pidIndex = command.indexOf(PID_SYMBOL);
        if (pidIndex < 0) {
            handler.handle("no pid");
            handler.handleEOF();
            return null;
        }
        int pidEndIndex = command.indexOf(' ', pidIndex + PID_SYMBOL.length());
        if (pidEndIndex < 0) {
            pidEndIndex = command.length();
        }
        String pidStr = command.substring(pidIndex + PID_SYMBOL.length(), pidEndIndex);
        int pid;
        try {
            pid = Integer.parseInt(pidStr);
        } catch (NumberFormatException e) {
            handler.handle("invalid pid [" + pidStr + "]");
            handler.handleEOF();
            return null;
        }

        String realCommand = command.substring(0, pidIndex) + command.substring(pidEndIndex);
        if (realCommand.endsWith(ASYNC_COMMAND_SYMBOL)) {
            handler.handle("not support async command");
            handler.handleEOF();
            return null;
        }

        if (header.getCode() == CommandCode.REQ_TYPE_PROFILER_START.getCode()) {
            //增加target profiler的存储文件夹参数
            realCommand = realCommand + " -s " + URLCoder.encode(BistouryStore.getRootStorePath());
        }

        return new ArthasTask(storeMapping.get(header.getCode()), header.getId(), header.getMaxRunningMs(), pid, realCommand, handler);
    }

}
