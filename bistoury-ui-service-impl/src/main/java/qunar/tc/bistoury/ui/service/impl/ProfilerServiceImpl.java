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
    public Profiler getRecord(String profilerId) {
        return profilerDao.getRecordById(profilerId);
    }

    @Override
    public List<Profiler> getLastRecords(String app, String agentId, int hours) {
        return profilerDao.getRecords(app, agentId, hours);
    }

    @Override
    public Profiler getLastProfilerRecord(String app, String agentId) {
        List<Profiler> records = profilerDao.getRecords(app, agentId, 1);
        if (records.isEmpty()) {
            return null;
        }
        return records.get(0);
    }

    @Override
    public List<Profiler> getRecordsByState(Profiler.State state, int hours) {
        return profilerDao.getRecordsByState(state, hours);
    }
}
