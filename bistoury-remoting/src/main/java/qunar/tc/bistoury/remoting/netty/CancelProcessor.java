package qunar.tc.bistoury.remoting.netty;

import com.google.common.collect.ImmutableList;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.List;

/**
 * @author zhenyu.nie created on 2018 2018/10/9 14:31
 */
public class CancelProcessor implements Processor<String> {

    private final TaskStore taskStore;

    public CancelProcessor(TaskStore taskStore) {
        this.taskStore = taskStore;
    }

    @Override
    public List<Integer> types() {
        return ImmutableList.of(CommandCode.REQ_TYPE_CANCEL.getCode());
    }

    @Override
    public void process(RemotingHeader header, String command, ResponseHandler handler) {
        taskStore.cancel(command);
    }
}
