package qunar.tc.bistoury.commands.host;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.command.ThreadCommand;
import qunar.tc.bistoury.remoting.netty.Task;
import qunar.tc.bistoury.remoting.netty.TaskFactory;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.Set;

/**
 * @author: leix.xie
 * @date: 2018/11/21 10:48
 * @describe：
 */
public class ThreadInfoTaskFactory implements TaskFactory<ThreadCommand> {
    private static final Logger logger = LoggerFactory.getLogger(ThreadInfoTaskFactory.class);

    private static final String NAME = "hostThreadInfo";

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_HOST_THREAD.getCode());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Task create(RemotingHeader header, ThreadCommand command, ResponseHandler handler) {
        logger.info("get thread info, thread command: {}", command);
        return new ThreadInfoTask(header.getId(), Integer.valueOf(command.getPid()), command.getThreadId(), command.getType(), command.getMaxDepth(), handler, header.getMaxRunningMs());
    }
}