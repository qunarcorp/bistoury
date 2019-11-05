package qunar.tc.bistoury.proxy.service;

import qunar.tc.bistoury.common.TypeResponse;

/**
 * @author cai.wen created on 2019/10/30 16:54
 */
public interface ProfilerStateManager {

    String register(String agentId, String command);

    boolean isProfilerRequest(String id);

    void dealProfiler(String profilesId, TypeResponse<String> config);

    void startProfiler(String profilesId);

    void forceStop(String profilerId);
}
