package qunar.tc.bistoury.commands.decompiler;

import com.google.common.collect.ImmutableSet;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.command.DecompilerCommand;
import qunar.tc.bistoury.remoting.netty.Task;
import qunar.tc.bistoury.remoting.netty.TaskFactory;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.Set;

/**
 * @author: leix.xie
 * @date: 2019/3/1 10:27
 * @describe：
 */
public class DecompilerTaskFactory implements TaskFactory<DecompilerCommand> {

    private static final String NAME = "decompiler";

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_DECOMPILER.getCode());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Task create(RemotingHeader header, DecompilerCommand command, ResponseHandler handler) {
        return new DecompilerTask(header.getId(), command, handler, header.getMaxRunningMs());
    }
}
