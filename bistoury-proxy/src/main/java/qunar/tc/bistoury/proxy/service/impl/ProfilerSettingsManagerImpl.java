package qunar.tc.bistoury.proxy.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerSettingsManager;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerSettingsStore;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class ProfilerSettingsManagerImpl implements ProfilerSettingsManager {

    private static final Joiner SPACE_JOINER = Joiner.on(" ").skipNulls();

    private static final String intervalKey = "-i";
    private static final String durationKey = "-d";
    private static final String eventKey = "-e";
    private static final String threadsKey = "-threads";
    private static final String modeKey = "-m";

    @Resource
    private ProfilerSettingsStore profilerSettingsStore;

    @Override
    public ProfilerSettings create(String appCode, Map<String, String> config) {
        String duration = config.getOrDefault(durationKey, profilerSettingsStore.getDurationSeconds(appCode));
        String interval = config.getOrDefault(intervalKey, profilerSettingsStore.getIntervalMillis(appCode));
        boolean threads = Boolean.parseBoolean(config.getOrDefault(threadsKey, String.valueOf(profilerSettingsStore.isThreads(appCode))));
        String event = config.getOrDefault(eventKey, profilerSettingsStore.getEvent(appCode));
        String modeCode = config.getOrDefault(eventKey, profilerSettingsStore.getModeCode(appCode));

        List<String> chunk = Lists.newArrayListWithExpectedSize(6);
        chunk.add(BistouryConstants.REQ_PROFILER_START);
        chunk.add(BistouryConstants.PROFILER_ID);
        chunk.add(durationKey);
        chunk.add(duration);
        chunk.add(intervalKey);
        chunk.add(interval);
        if (threads) {
            chunk.add(threadsKey);
        }
        if (event != null) {
            chunk.add(eventKey);
            chunk.add(event);
        }
        if (modeCode != null) {
            chunk.add(modeKey);
            chunk.add(modeCode);
        }
        return new ProfilerSettings(appCode, Integer.parseInt(duration), Integer.parseInt(interval),
                modeCode == null ? Profiler.Mode.async_sampler.code : Integer.parseInt(modeCode),
                SPACE_JOINER.join(chunk));
    }
}
