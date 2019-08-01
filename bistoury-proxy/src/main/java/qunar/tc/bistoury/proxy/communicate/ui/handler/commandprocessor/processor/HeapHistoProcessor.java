package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.processor;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
import qunar.tc.bistoury.remoting.command.HeapHistoCommand;
import qunar.tc.bistoury.remoting.protocol.CommandCode;

import java.util.Set;

/**
 * @author leix.xie
 * @date 2019/5/22 16:10
 * @describe
 */
@Service
public class HeapHistoProcessor extends AbstractCommand<HeapHistoCommand> {

    @Override
    protected HeapHistoCommand prepareCommand(RequestData<HeapHistoCommand> data, String agentId) {
        HeapHistoCommand command = data.getCommand();
        command.setPid(BistouryConstants.FILL_PID);
        return command;
    }

    @Override
    public Set<Integer> getCodes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_HOST_HEAP_HISTO.getCode());
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
