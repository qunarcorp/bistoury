package qunar.tc.bistoury.commands.cpujstack;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.kv.KvDb;
import qunar.tc.bistoury.agent.common.kv.KvDbs;
import qunar.tc.bistoury.agent.common.util.DateUtils;
import qunar.tc.bistoury.remoting.netty.Task;
import qunar.tc.bistoury.remoting.netty.TaskFactory;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/1/9 19:34
 */
public class ThreadInfoTaskFactory implements TaskFactory<String> {

    private static final Logger logger = LoggerFactory.getLogger(ThreadInfoTaskFactory.class);

    private static final KvDb kvDb = KvDbs.getKvDb();
    private static final String NAME = "threadInfo";

    @Override
    public Set<Integer> codes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_CPU_JSTACK_THREADS.getCode());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Task create(RemotingHeader header, String command, ResponseHandler handler) {
        DateUtils.TIME_FORMATTER.parseLocalDate(command);
        return new ThreadInfoTask(header.getId(), header.getMaxRunningMs(), kvDb, handler, command);
    }
}
