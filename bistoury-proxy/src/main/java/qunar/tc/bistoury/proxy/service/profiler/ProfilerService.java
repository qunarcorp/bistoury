package qunar.tc.bistoury.proxy.service.profiler;

import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

/**
 * @author cai.wen created on 2019/10/30 14:50
 */
public interface ProfilerService {

    void startProfiler(String profilerId);

    String prepareProfiler(String agentId, ProfilerSettings profilerSettings);

    Profiler getProfilerRecord(String profilerId);

    void stopProfiler(String profilesId);

    void stopWithError(String profilerId);
}
