package qunar.tc.bistoury.commands.host;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.netty.Task;
import qunar.tc.bistoury.remoting.netty.TaskFactory;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.Set;


/**
 * @author: leix.xie
 * @date: 2018/11/15 14:37
 * @describe：
 */
public class HostTaskFactory implements TaskFactory<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(HostTaskFactory.class);
    private static final String NAME = "host";

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_HOST_JVM.getCode());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Task create(RemotingHeader header, Integer command, ResponseHandler handler) {
        return new HostTask(header.getId(), command, handler, header.getMaxRunningMs());
    }
}
