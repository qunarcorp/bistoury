package qunar.tc.bistoury.proxy.service.impl;

import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerManager;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerService;
import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

import javax.annotation.Resource;

/**
 * @author cai.wen created on 2019/10/30 16:54
 */
@Service
public class DefaultProfilerManager implements ProfilerManager {

    @Resource
    private ProfilerService profilerService;

    @Override
    public String prepare(String agentId, ProfilerSettings settings) {
        String profilerId = profilerService.prepareProfiler(agentId, settings);
        String command = settings.getCommand().replace(BistouryConstants.PROFILER_ID, profilerId)
                + BistouryConstants.PID_PARAM + BistouryConstants.FILL_PID;
        settings.setCommand(command);
        return profilerId;
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

