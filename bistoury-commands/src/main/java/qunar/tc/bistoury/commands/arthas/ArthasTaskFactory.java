package qunar.tc.bistoury.commands.arthas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.commands.arthas.telnet.ArthasTelnetStore;
import qunar.tc.bistoury.commands.arthas.telnet.DebugTelnetStore;
import qunar.tc.bistoury.commands.arthas.telnet.TelnetStore;
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

    private static final TelnetStore arthasTelnetStore = ArthasTelnetStore.getInstance();

    private static final TelnetStore debugTelnetStore = DebugTelnetStore.getInstance();


    private static final Map<Integer, TelnetStore> storeMapping;

    static {
        storeMapping = ImmutableMap.<Integer, TelnetStore>builder()
                .put(CommandCode.REQ_TYPE_ARTHAS.getCode(), arthasTelnetStore)
                .put(CommandCode.REQ_TYPE_DEBUG.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_JAR_DEBUG.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_MONITOR.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_JAR_INFO.getCode(), debugTelnetStore)
                .put(CommandCode.REQ_TYPE_CONFIG.getCode(), debugTelnetStore)
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

        return new ArthasTask(storeMapping.get(header.getCode()), header.getId(), header.getMaxRunningMs(), pid, realCommand, handler);
    }
}
