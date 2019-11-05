package qunar.tc.bistoury.proxy.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qunar.tc.bistoury.proxy.service.ProfilerService;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.dao.ProfilerDao;
import qunar.tc.bistoury.serverside.dao.ProfilerDaoImpl;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen created on 2019/10/31 16:21
 */
@Service
public class ProfilerServiceImpl implements ProfilerService {

    private final ProfilerDao profilerDao = new ProfilerDaoImpl();

    private static final Object obj = new Object();

    private final Cache<String, Object> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build();

    @Override
    public void startProfiler(String profilerId) {
        profilerDao.startProfiler(profilerId);
    }

    @Override
    public void deleteProfiler(String profilerId) {
        profilerDao.deleteProfiler(profilerId);
    }

    @Override
    @Transactional
    public String prepareProfiler(String agentId, int duration) {
        checkLastPrepareId(agentId);
        String profilerId = UUID.randomUUID().toString().replace("-", "");
        Profiler profiler = new Profiler();
        profiler.setAgentId(agentId);
        profiler.setProfilerId(profilerId);
        profiler.setStartTime(new Timestamp(System.currentTimeMillis()));
        profiler.setState(Profiler.State.ready);
        profiler.setDuration(duration);
        profiler.setOperator("");
        profiler.setAppCode("");
        profilerDao.prepareProfiler(profiler);
        cache.put(profilerId, obj);
        return profilerId;
    }

    private void checkLastPrepareId(String agentId) {
        Profiler profiler = profilerDao.getLastProfilerRecord("", agentId);
        if (profiler != null && profiler.getState() == Profiler.State.ready) {
            if (profiler.getStartTime().getTime() - System.currentTimeMillis() > 3 * 60 * 1000) {
                profilerDao.deleteProfiler(profiler.getProfilerId());
                return;
            }
            throw new RuntimeException("agent is already prepare profiler.");
        }
    }

    @Override
    public boolean isPrepareProfilerId(String profilerId) {
        return cache.getIfPresent(profilerId) != null;
    }

    @Override
    public void changeState(String profilerId, Profiler.State state) {
        profilerDao.changeState(state, profilerId);
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
    public Profiler getProfilerRecord(String profilerId) {
        return profilerDao.getProfilerRecord(profilerId);
    }

    @Override
    public void stopProfiler(String profilesId) {
        profilerDao.stopProfiler(profilesId);
    }
}
