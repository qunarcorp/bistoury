package qunar.tc.bistoury.proxy.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.proxy.dao.ProfilerLockDao;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerManager;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerService;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

import javax.annotation.Resource;

/**
 * @author cai.wen created on 2019/10/30 16:54
 */
@Service
public class DefaultProfilerManager implements ProfilerManager {

    @Resource
    private ProfilerService profilerService;

    @Resource
    private ProfilerLockDao profilerLockDao;

    @Override
    @Transactional
    public String prepare(String agentId, ProfilerSettings settings) {
        profilerLockDao.insert(settings.getAppCode(), agentId);
        String profilerId = profilerService.prepareProfiler(agentId, settings);
        String command = settings.getCommand().replace(BistouryConstants.PROFILER_ID, profilerId)
                + BistouryConstants.PID_PARAM + BistouryConstants.FILL_PID;
        settings.setCommand(command);
        return profilerId;
    }

    @Override
    @Transactional
    public void start(String profilerId) {
        profilerService.startProfiler(profilerId);
    }

    @Override
    @Transactional
    public void stop(String profilerId) {
        if (isStopped(profilerId)) {
            return;
        }
        Profiler profiler = profilerService.getProfilerRecord(profilerId);
        profilerLockDao.delete(profiler.getAppCode(), profiler.getAgentId());
        profilerService.stopProfiler(profilerId);
    }

    @Override
    @Transactional
    public void stopWithError(String profilerId) {
        Profiler profiler = profilerService.getProfilerRecord(profilerId);
        profilerLockDao.delete(profiler.getAppCode(), profiler.getAgentId());
        profilerService.stopWithError(profilerId);
    }

    private boolean isStopped(String profilerId) {
        Profiler profiler = profilerService.getProfilerRecord(profilerId);
        Profiler.State state = profiler.getState();
        return state != Profiler.State.ready && state != Profiler.State.start;
    }
}

