package qunar.tc.bistoury.commands.profiler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListeningExecutorService;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.util.ProfilerUtils;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.command.ProfilerSearchCommand;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Processor;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static qunar.tc.bistoury.remoting.protocol.CommandCode.REQ_TYPE_PROFILER_FILE_SEARCH;

/**
 * @author cai.wen created on 2019/10/31 13:13
 */
public class ProfilerFileSearchProcessor implements Processor<ProfilerSearchCommand> {

    private static final ListeningExecutorService agentExecutor = AgentRemotingExecutor.getExecutor();

    private static final String taskName = "profilerfilesearch";

    @Override
    public List<Integer> types() {
        return ImmutableList.of(CommandCode.REQ_TYPE_PROFILER_FILE_SEARCH.getCode());
    }

    @Override
    public void process(RemotingHeader header, final ProfilerSearchCommand command, final ResponseHandler handler) {
        agentExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                queryProfiler(command.getId(), handler);
                return null;
            }
        });
    }

    private void queryProfiler(String profilerId, ResponseHandler handler) {
        boolean isDone = ProfilerUtils.isDone(profilerId);
        handlerSuccess(handler, isDone);
    }

    private void handlerSuccess(ResponseHandler handler, boolean data) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(3);
        result.put("type", taskName);
        result.put("code", 0);
        result.put("data", data);
        handler.handle(REQ_TYPE_PROFILER_FILE_SEARCH.getCode(), JacksonSerializer.serialize(result));
    }
}
