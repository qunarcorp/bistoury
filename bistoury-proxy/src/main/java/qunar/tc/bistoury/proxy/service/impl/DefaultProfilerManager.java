package qunar.tc.bistoury.proxy.service.impl;

import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerManager;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerService;
import qunar.tc.bistoury.remoting.protocol.Datagram;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

import javax.annotation.Resource;

import static qunar.tc.bistoury.proxy.util.ProfilerDatagramHelper.createFinishStateSearchDatagram;
import static qunar.tc.bistoury.proxy.util.ProfilerDatagramHelper.createStopDatagram;

/**
 * @author cai.wen created on 2019/10/30 16:54
 */
@Service
public class DefaultProfilerManager implements ProfilerManager {

    @Resource
    private ProfilerService profilerService;

    @Resource
    private AgentConnectionStore agentConnectionStore;

    @Override
    public String prepare(String agentId, ProfilerSettings settings) {
        String profilerId = profilerService.prepareProfiler(agentId, settings);
        String command = settings.getCommand().replace(BistouryConstants.PROFILER_ID, profilerId)
                + BistouryConstants.PID_PARAM + BistouryConstants.FILL_PID;
        settings.setCommand(command);
        return profilerId;
    }

    @Override
    public void searchStopState(String profilerId) {
        Profiler profiler = profilerService.getProfilerRecord(profilerId);
        String agentId = profiler.getAgentId();
        Datagram datagram = createFinishStateSearchDatagram(profilerId);
        agentConnectionStore.getConnection(agentId)
                .ifPresent(agentConn -> agentConn.write(datagram));
    }

    @Override
    public void forceStop(String agentId, String profilerId) {
        Datagram datagram = createStopDatagram(profilerId);
        agentConnectionStore.getConnection(agentId)
                .ifPresent(agentConn -> agentConn.write(datagram));
    }

    @Override
    public void start(String profilerId) {
        profilerService.startProfiler(profilerId);
    }

    @Override
    public void stop(String profilerId) {
        profilerService.stopProfiler(profilerId);
    }

    @Override
    public void stopWithError(String profilerId) {
        profilerService.stopWithError(profilerId);
    }
}

