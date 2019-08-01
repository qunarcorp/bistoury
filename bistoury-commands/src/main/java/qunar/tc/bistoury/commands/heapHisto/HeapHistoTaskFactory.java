package qunar.tc.bistoury.commands.heapHisto;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.command.HeapHistoCommand;
import qunar.tc.bistoury.remoting.netty.Task;
import qunar.tc.bistoury.remoting.netty.TaskFactory;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.Set;

/**
 * @author: leix.xie
 * @date: 2018/12/10 14:35
 * @describe：
 */
public class HeapHistoTaskFactory implements TaskFactory<HeapHistoCommand> {
    private static final Logger logger = LoggerFactory.getLogger(HeapHistoTaskFactory.class);

    private static final String NAME = "heapHisto";

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_HOST_HEAP_HISTO.getCode());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Task create(RemotingHeader header, HeapHistoCommand command, ResponseHandler handler) {
        final String param = command.getParam();
        final long selectTimestamp = command.getTimestamp();
        int pid = Integer.valueOf(command.getPid());
        logger.info("get heap histo command: {}", command);
        HeapHistoTask task = new HeapHistoTask(header.getId(), pid, selectTimestamp, param, handler, header.getMaxRunningMs());
        return task;
    }
}
