package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.processor;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
import qunar.tc.bistoury.remoting.protocol.CommandCode;

import java.util.Set;

/**
 * @author leix.xie
 * @date 2019/5/22 15:18
 * @describe
 */
@Service
public class HostInfoProcessor extends AbstractCommand<String> {

    @Override
    protected String prepareCommand(RequestData data, String agentId) {
        return BistouryConstants.FILL_PID;
    }

    @Override
    public Set<Integer> getCodes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_HOST_JVM.getCode());
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
