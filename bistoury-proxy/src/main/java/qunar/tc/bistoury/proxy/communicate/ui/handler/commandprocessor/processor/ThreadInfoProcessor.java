package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.processor;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
import qunar.tc.bistoury.remoting.command.ThreadCommand;

import java.util.Set;

import static qunar.tc.bistoury.remoting.protocol.CommandCode.REQ_TYPE_HOST_THREAD;

/**
 * @author leix.xie
 * @date 2019/5/22 16:57
 * @describe
 */
@Service
public class ThreadInfoProcessor extends AbstractCommand<ThreadCommand> {

    @Override
    protected ThreadCommand prepareCommand(RequestData<ThreadCommand> data, String agentId) {
        ThreadCommand command = data.getCommand();
        command.setPid(BistouryConstants.FILL_PID);
        return command;
    }

    @Override
    public Set<Integer> getCodes() {
        return ImmutableSet.of(REQ_TYPE_HOST_THREAD.getCode());
    }

    @Override
    public int getMinAgentVersion() {
        return -1;
    }

    @Override
    public boolean supportMulti() {
        return false;
    }

}
