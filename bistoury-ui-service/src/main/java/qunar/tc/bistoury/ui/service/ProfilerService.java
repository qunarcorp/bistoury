package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.serverside.bean.Profiler;

import java.util.List;

/**
 * @author cai.wen created on 2019/11/4 12:52
 */
public interface ProfilerService {

    Profiler getProfilerRecord(String profilerId);

    List<Profiler> getProfilerRecords(String app, String agentId);

    Profiler getLastProfilerRecord(String app, String agentId);

    List<Profiler> getProfilersByState(int state);
}
