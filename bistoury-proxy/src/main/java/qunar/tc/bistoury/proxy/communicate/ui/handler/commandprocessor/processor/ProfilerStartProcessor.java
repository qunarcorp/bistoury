package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.processor;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerManager;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerSettingsManager;
import qunar.tc.bistoury.proxy.util.ProfilerDatagramHelper;
import qunar.tc.bistoury.remoting.protocol.CommandCode;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class ProfilerStartProcessor extends AbstractCommand<String> {

    private static final Splitter SPCAE_SPLITTER = Splitter.on(" ").trimResults();

    @Resource
    private ProfilerManager profilerManager;

    @Resource
    private ProfilerSettingsManager profilerSettingsManager;

    @Override
    public Set<Integer> getCodes() {
        return ImmutableSet.of(CommandCode.REQ_TYPE_PROFILER_START.getCode());
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
        String command = data.getCommand();
        ProfilerSettings settings = profilerSettingsManager.create(getAppCode(command), getConfig(command));
        profilerManager.prepare(agentId, settings);
        return settings.getCommand();
    }

    private String getAppCode(String command) {
        return SPCAE_SPLITTER.splitToList(command).get(1);
    }

    private Map<String, String> getConfig(String command) {
        String duration = SPCAE_SPLITTER.splitToList(command).get(2);
        return ImmutableMap.of("-d", duration);
    }

    @Override
    public Datagram prepareResponse(Datagram datagram) {
        Optional<String> profilerIdRef = ProfilerDatagramHelper.getChangedProfilerId(datagram);
        profilerIdRef.ifPresent(profilerId -> profilerManager.start(profilerId));
        return datagram;
    }
}

