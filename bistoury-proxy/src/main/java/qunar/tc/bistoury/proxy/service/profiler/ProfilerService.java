package qunar.tc.bistoury.proxy.service.profiler;

import qunar.tc.bistoury.serverside.bean.Profiler;

/**
 * @author cai.wen created on 2019/10/30 14:50
 */

public interface ProfilerService {

    void startProfiler(String profilerId);

    String prepareProfiler(String agentId, int duration, int frequency, Profiler.Mode mode);

    Profiler getProfilerRecord(String profilerId);

    void stopProfiler(String profilesId);
}
