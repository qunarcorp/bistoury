package qunar.tc.bistoury.commands.monitor;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.command.MonitorCommand;
import qunar.tc.bistoury.remoting.netty.Task;
import qunar.tc.bistoury.remoting.netty.TaskFactory;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.Set;

/**
 * @author: leix.xie
 * @date: 2019/1/9 15:10
 * @describe：
 */
public class QMonitorQueryTaskFactory implements TaskFactory<MonitorCommand> {
    private static final Logger logger = LoggerFactory.getLogger(QMonitorQueryTaskFactory.class);

    private static final String NAME = "qmonitorQuery";

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_QMONITOR_QUERY.getCode());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Task create(RemotingHeader header, MonitorCommand command, ResponseHandler handler) {
        return new QMonitorQueryTask(header.getId(), command, handler, header.getMaxRunningMs());
    }
}
