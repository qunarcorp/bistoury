package qunar.tc.bistoury.proxy.service.profiler;

import qunar.tc.bistoury.serverside.bean.Profiler;

import java.util.List;

/**
 * @author cai.wen created on 2019/10/30 14:50
 */

public interface ProfilerService {

    void startProfiler(String profilerId);

    void deleteProfiler(String profilerId);

    String prepareProfiler(String agentId, int duration);

    boolean isPrepareProfilerId(String profilerId);

    void changeState(String profilerId, Profiler.State state);

    List<Profiler> getProfilerRecords(String app, String agentId);

    Profiler getLastProfilerRecord(String app, String agentId);

    Profiler getProfilerRecord(String profilerId);

    void stopProfiler(String profilesId);
}
