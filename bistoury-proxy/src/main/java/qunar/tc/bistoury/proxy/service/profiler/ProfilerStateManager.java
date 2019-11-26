package qunar.tc.bistoury.proxy.service.profiler;

import qunar.tc.bistoury.common.TypeResponse;
import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

import java.util.Map;

/**
 * @author cai.wen created on 2019/10/30 16:54
 */
public interface ProfilerStateManager {

    ProfilerSettings register(String agentId, String command);

    boolean isProfilerRequest(String id);

    void dealProfiler(String profilesId, TypeResponse<Map<String, String>> config);

    void searchStopState(String profilerId);

    void forceStop(String agentId, String profilerId);
}
