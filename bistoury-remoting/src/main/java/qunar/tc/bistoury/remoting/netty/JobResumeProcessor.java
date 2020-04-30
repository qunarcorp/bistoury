package qunar.tc.bistoury.remoting.netty;

import com.google.common.collect.ImmutableList;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.agent.common.job.ResponseJobStore;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.List;

/**
 * @author zhenyu.nie created on 2019 2019/10/31 14:53
 */
public class JobResumeProcessor implements Processor<String> {

    private final ResponseJobStore jobStore;

    public JobResumeProcessor(ResponseJobStore jobStore) {
        this.jobStore = jobStore;
    }

    @Override
    public List<Integer> types() {
        return ImmutableList.of(CommandCode.REQ_TYPE_JOB_RESUME.getCode());
    }

    @Override
    public void process(RemotingHeader header, String command, ResponseHandler handler) {
        jobStore.resume(command);
    }
}
