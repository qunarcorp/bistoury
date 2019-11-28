package qunar.tc.bistoury.proxy.service.profiler;

import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

import java.util.Map;

public interface ProfilerSettingsManager {

    ProfilerSettings create(String appCode, Map<String, String> config);
}
