package qunar.tc.bistoury.proxy.service.profiler;

public interface ProfilerSettingsStore {

    String getDurationSeconds(String appCode);

    String getIntervalMillis(String appCode);

    boolean isThreads(String appCode);

    String getEvent(String appCode);

    String getModeCode(String appCode);
}
