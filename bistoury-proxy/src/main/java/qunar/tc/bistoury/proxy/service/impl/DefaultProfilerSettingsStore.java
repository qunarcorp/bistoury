package qunar.tc.bistoury.proxy.service.impl;

import org.springframework.stereotype.Service;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerSettingsStore;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class DefaultProfilerSettingsStore implements ProfilerSettingsStore {

    private volatile Map<String, String> profilerConfig;

    private static final String DEFAULT = "default";

    private static final String DURATION_SUFFIX = ".duration";

    private static final String FREQUENCY_SUFFIX = ".frequency";

    private static final String THREADS_SUFFIX = ".threads";

    private static final String EVENT_SUFFIX = ".event";

    private static final String MODE_SUFFIX = ".mode";

    @PostConstruct
    public void init() {
        DynamicConfigLoader.<LocalDynamicConfig>load("profiler.properties")
                .addListener(conf -> profilerConfig = conf.asMap());
    }

    @Override
    public String getDurationSeconds(String appCode) {
        String duration = profilerConfig.get(appCode + DURATION_SUFFIX);
        return duration == null ? profilerConfig.get(DEFAULT + DURATION_SUFFIX) : duration;
    }

    @Override
    public String getFrequencyMillis(String appCode) {
        String frequency = profilerConfig.get(appCode + FREQUENCY_SUFFIX);
        return frequency == null ? profilerConfig.get(DEFAULT + FREQUENCY_SUFFIX) : frequency;
    }

    @Override
    public boolean isThreads(String appCode) {
        String isThreads = profilerConfig.get(appCode + THREADS_SUFFIX);
        return Boolean.parseBoolean(isThreads == null ? profilerConfig.get(DEFAULT + THREADS_SUFFIX) : isThreads);
    }

    @Override
    public String getEvent(String appCode) {
        String event = profilerConfig.get(appCode + EVENT_SUFFIX);
        return event == null ? profilerConfig.get(DEFAULT + EVENT_SUFFIX) : event;
    }

    @Override
    public String getModeCode(String appCode) {
        String modeCode = profilerConfig.get(appCode + MODE_SUFFIX);
        return modeCode == null ? profilerConfig.get(DEFAULT + MODE_SUFFIX) : modeCode;
    }
}
