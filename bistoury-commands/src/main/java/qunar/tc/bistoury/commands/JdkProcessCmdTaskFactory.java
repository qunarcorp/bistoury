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

    