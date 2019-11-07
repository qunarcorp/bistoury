package qunar.tc.bistoury.serverside.dao;

import qunar.tc.bistoury.serverside.bean.Profiler;

import java.util.List;

/**
 * @author cai.wen created on 2019/10/30 14:52
 */
public interface ProfilerDao {

    List<Profiler> getRecords(String app, String agentId, int hours);

    void changeState(Profiler.State state, String profilerId);

    void prepareProfiler(Profiler profiler);

    List<Profiler> getRecordsByState(Profiler.State state, int hours);

    Profiler getRecordById(String profilerId);
}
