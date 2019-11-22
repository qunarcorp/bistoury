package qunar.tc.bistoury.proxy.service.profiler;

import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

public interface ProfilerSettingsManager {

    ProfilerSettings create(String command);
}
