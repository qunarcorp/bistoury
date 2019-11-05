package qunar.tc.bistoury.commands.profiler;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListeningExecutorService;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.util.ProfilerUtils;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.CodeProcessResponse;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.common.TypeResponse;
import qunar.tc.bistoury.remoting.command.ProfilerSearchCommand;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;
import qunar.tc.bistoury.remoting.netty.Processor;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.List;
import java.util.concurrent.Callable;

import static qunar.tc.bistoury.remoting.protocol.CommandCode.REQ_TYPE_PROFILER_FINISH_STATE_SEARCH;
import static qunar.tc.bistoury.remoting.protocol.CommandCode.REQ_TYPE_PROFILER_TEMP_FILE_SEARCH;

/**
 * @author cai.wen created on 2019/10/31 13:13
 */
public class ProfilerFileSearchProcessor implements Processor<ProfilerSearchCommand> {

    private static final ListeningExecutorService agentExecutor = AgentRemotingExecutor.getExecutor();

    private static final String taskName = "profilerfilesearch";

    @Override
    public List<Integer> types() {
        return ImmutableList.of(
                CommandCode.REQ_TYPE_PROFILER_FINISH_STATE_SEARCH.getCode(),
                CommandCode.REQ_TYPE_PROFILER_TEMP_FILE_SEARCH.getCode());
    }

    @Override
    public void process(final RemotingHeader header, final ProfilerSearchCommand profiler, final ResponseHandler handler) {
        agentExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                queryProfiler(profiler.getId(), handler, header.getCode());
                return null;
            }
        });
    }

    private void queryProfiler(String profilerId, ResponseHandler handler, int code) {
        boolean isDone = false;
        if (code == REQ_TYPE_PROFILER_FINISH_STATE_SEARCH.getCode()) {
            isDone = ProfilerUtils.isDone(profilerId);
        } else if (code == REQ_TYPE_PROFILER_TEMP_FILE_SEARCH.getCode()) {
            isDone = ProfilerUtils.isStart(profilerId);
        }
        CodeProcessResponse<String> response = new CodeProcessResponse<>();
        TypeResponse<String> typeResponse = new TypeResponse<>();
        typeResponse.setType(BistouryConstants.REQ_PROFILER_FINISH_STATE_SEARCH);
        typeResponse.setData(response);
        response.setData(Boolean.toString(isDone));
        handler.handle(JacksonSerializer.serializeToBytes(typeResponse));
    }

}
