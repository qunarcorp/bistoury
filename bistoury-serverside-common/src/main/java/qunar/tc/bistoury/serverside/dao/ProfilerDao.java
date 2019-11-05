package qunar.tc.bistoury.serverside.dao;

import qunar.tc.bistoury.serverside.bean.Profiler;

import java.util.List;

/**
 * @author cai.wen created on 2019/10/30 14:52
 */
public interface ProfilerDao {

    List<Profiler> getProfilerRecords(String app, String agentId);

    Profiler getLastProfilerRecord(String app, String agentId);

    Profiler getProfilerRecord(String profilerId);

    void stopProfiler(String profilerId);

    void changeState(Profiler.State state, String profilerId);

    void deleteProfiler(String profilerId);

    void startProfiler(String profilerId);

    void prepareProfiler(Profiler profiler);

    List<Profiler> getProfilersByState(int state);
}
