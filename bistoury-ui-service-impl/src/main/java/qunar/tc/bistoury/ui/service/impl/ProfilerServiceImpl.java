package qunar.tc.bistoury.ui.service.impl;

import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.ui.dao.ProfilerDao;
import qunar.tc.bistoury.ui.service.ProfilerService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author cai.wen created on 2019/11/4 12:54
 */
@Service
public class ProfilerServiceImpl implements ProfilerService {

    @Resource
    private ProfilerDao profilerDao;

    @Override
    public Profiler getRecord(String profilerId) {
        return profilerDao.getRecordByProfilerId(profilerId);
    }

    @Override
    public List<Profiler> getLastRecords(String app, String agentId, LocalDateTime startTime) {
        return profilerDao.getRecords(app, agentId, startTime);
    }

    @Override
    public Optional<Profiler> getLastProfilerRecord(String app, String agentId) {
        return profilerDao.getLastRecord(app, agentId);
    }
}
