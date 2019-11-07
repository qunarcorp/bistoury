package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.serverside.bean.Profiler;

import java.util.List;

/**
 * @author cai.wen created on 2019/11/4 12:52
 */
public interface ProfilerService {

    Profiler getRecord(String profilerId);

    List<Profiler> getLastRecords(String app, String agentId, int hours);

    Profiler getLastProfilerRecord(String app, String agentId);

    List<Profiler> getRecordsByState(Profiler.State state, int hours);
}
