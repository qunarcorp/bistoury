package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.processor;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.common.TypeResponse;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerManager;
import qunar.tc.bistoury.proxy.util.ProfilerDatagramHelper;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.Datagram;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class ProfilerStopProcessor extends AbstractCommand<String> {

    @Resource
    private ProfilerManager profilerManager;

    @Override
    public Set<Integer> getCodes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_PROFILER_STOP.getCode());
    }

    @Override
    public int getMinAgentVersion() {
        return 0;
    }

    @Override
    public boolean supportMulti() {
        return false;
    }

    @Override
    protected String prepareCommand(RequestData<String> data, String agentId) {
        return data.getCommand() + BistouryConstants.PID_PARAM + BistouryConstants.FILL_PID;
    }

    @Override
    public Datagram prepareResponse(Datagram datagram) {
        Optional<String> profilerIdRef = ProfilerDatagramHelper.getChangedProfilerId(datagram);
        profilerIdRef.ifPresent(s -> profilerManager.stop(s));
        return datagram;
    }
}
