package qunar.tc.bistoury.proxy.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.proxy.dao.ProfilerDao;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerService;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen created on 2019/10/31 16:21
 */
@Service
public class ProfilerServiceImpl implements ProfilerService {

    @Resource
    private ProfilerDao profilerDao;

    private static final Object obj = new Object();

    private final Cache<String, Object> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build();

    @Override
    public void startProfiler(String profilerId) {
        profilerDao.changeState(Profiler.State.start, profilerId);
    }

    @Override
    public String prepareProfiler(String agentId, ProfilerSettings settings) {
        String profilerId = UUID.randomUUID().toString().replace("-", "");
        Profiler profiler = new Profiler();
        profiler.setAppCode(settings.getAppCode());
        profiler.setAgentId(agentId);
        profiler.setProfilerId(profilerId);
        profiler.setStartTime(new Timestamp(System.currentTimeMillis()));
        profiler.setState(Profiler.State.ready);
        profiler.setDuration(settings.getDuration());
        profiler.setInterval(settings.getInterval());
        profiler.setMode(Profiler.Mode.fromCode(settings.getMode()));
        profiler.setOperator("");
        profilerDao.prepareProfiler(profiler);
        cache.put(profilerId, obj);
        return profilerId;
    }

    @Override
    public Profiler getProfilerRecord(String profilerId) {
        return profilerDao.getRecordByProfilerId(profilerId);
    }

    @Override
    public void stopProfiler(String profilerId) {
        profilerDao.changeState(Profiler.State.stop, profilerId);
    }

    @Override
    public void stopWithError(String profilerId) {
        profilerDao.changeState(Profiler.State.error, profilerId);
    }
}
