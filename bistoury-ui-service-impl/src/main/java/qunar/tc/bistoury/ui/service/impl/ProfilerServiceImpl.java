package qunar.tc.bistoury.ui.service.impl;

import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.dao.ProfilerDao;
import qunar.tc.bistoury.serverside.dao.ProfilerDaoImpl;
import qunar.tc.bistoury.ui.service.ProfilerService;

import java.util.List;

/**
 * @author cai.wen created on 2019/11/4 12:54
 */
@Service
public class ProfilerServiceImpl implements ProfilerService {

    private final ProfilerDao profilerDao = new ProfilerDaoImpl();

    @Override
    public Profiler getProfilerRecord(String profilerId) {
        return profilerDao.getProfilerRecord(profilerId);
    }

    @Override
    public List<Profiler> getProfilerRecords(String app, String agentId) {
        return profilerDao.getProfilerRecords(app, agentId);
    }

    @Override
    public Profiler getLastProfilerRecord(String app, String agentId) {
        return profilerDao.getLastProfilerRecord(app, agentId);
    }

    @Override
    public List<Profiler> getProfilersByState(int state) {
        return profilerDao.getProfilersByState(state);
    }
}
