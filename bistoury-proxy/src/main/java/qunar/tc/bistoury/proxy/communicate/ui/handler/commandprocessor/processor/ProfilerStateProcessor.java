package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.processor;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
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

import static qunar.tc.bistoury.common.BistouryConstants.REQ_PROFILER_FINNSH_STATE_SEARCH;
import static qunar.tc.bistoury.common.BistouryConstants.REQ_PROFILER_START_STATE_SEARCH;

@Service
public class ProfilerStateProcessor extends AbstractCommand<String> {

    @Resource
    private ProfilerManager profilerManager;

    @Override
    public Set<Integer> getCodes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_PROFILER_STATE_SEARCH.getCode());
    }

    @Override
    public int getMinAgentVersion() {
        return -1;
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
        if (profilerIdRef.isPresent()) {
            String type = ProfilerDatagramHelper.getStateSearchType(datagram);
            if (REQ_PROFILER_START_STATE_SEARCH.equals(type)) {
                profilerManager.start(profilerIdRef.get());
            } else if (REQ_PROFILER_FINNSH_STATE_SEARCH.equals(type)) {
                profilerManager.stop(profilerIdRef.get());
            }
        }
        return datagram;
    }

    private String getStateSearchType(Map<String, String> data) {
        return data.get("type");
    }
}
