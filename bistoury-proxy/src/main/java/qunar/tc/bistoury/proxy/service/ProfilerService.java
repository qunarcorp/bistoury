package qunar.tc.bistoury.proxy.service;

import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.dao.ProfilerDao;
import qunar.tc.bistoury.serverside.dao.ProfilerDaoImpl;

import java.util.Date;
import java.util.UUID;

/**
 * @author cai.wen created on 2019/10/30 14:50
 */
@Service
public class ProfilerService {

    private final ProfilerDao profilerDao = new ProfilerDaoImpl();

    public String startProfiler(String agentId) {
        String profilerId = UUID.randomUUID().toString().replace("-", "");
        Profiler profiler = new Profiler();
        profiler.setAgentId(agentId);
        profiler.setProfilerId(profilerId);
        profiler.setStartTime(new Date());
        profiler.setState(Profiler.State.start);
        profiler.setOperator("");
        profiler.setAppCode("");
        profilerDao.startProfiler(profiler);
        return profilerId;
    }

}
