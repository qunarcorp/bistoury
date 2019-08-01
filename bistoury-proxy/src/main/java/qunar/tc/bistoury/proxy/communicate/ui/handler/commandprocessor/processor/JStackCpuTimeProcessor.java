package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.processor;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
import qunar.tc.bistoury.remoting.command.CpuTimeCommand;

import java.util.Set;

import static qunar.tc.bistoury.remoting.protocol.CommandCode.REQ_TYPE_CPU_JSTACK_TIMES;

/**
 * @author leix.xie
 * @date 2019/5/27 10:43
 * @describe
 */
@Service
public class JStackCpuTimeProcessor extends AbstractCommand<CpuTimeCommand> {

    @Override
    public Set<Integer> getCodes() {
        return ImmutableSet.of(REQ_TYPE_CPU_JSTACK_TIMES.getCode());
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
