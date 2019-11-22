package qunar.tc.bistoury.proxy.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.BistouryConstants;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerSettingsManager;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerSettingsStore;
import qunar.tc.bistoury.serverside.agile.Strings;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class ProfilerSettingsManagerImpl implements ProfilerSettingsManager {

    private static final Splitter SPACE_SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();

    private static final Joiner SPACE_JOINER = Joiner.on(" ").skipNulls();

    @Resource
    private ProfilerSettingsStore profilerSettingsStore;

    @Override
    public ProfilerSettings create(String command) {
        if (Strings.isEmpty(command)) {
            return null;
        }
        return getReaLCommand(SPACE_SPLITTER.splitToList(command));
    }

    private static final String frequencyKey = "-f";

    private static final String durationKey = "-d";

    private static final String eventKey = "-e";

    private static final String threadsKey = "-threads";

    private static final String modeKey = "-m";

    private ProfilerSettings getReaLCommand(List<String> commandChunk) {
        String appCode = commandChunk.get(1);
        Map<String, String> params = getCommandParams(commandChunk.subList(2, commandChunk.size()));
        return doGetReaLCommand(appCode, params);
    }

    private ProfilerSettings doGetReaLCommand(String appCode, Map<String, String> params) {
        String duration = params.getOrDefault(durationKey, profilerSettingsStore.getDurationSeconds(appCode));
        String frequency = params.getOrDefault(frequencyKey, profilerSettingsStore.getFrequencyMillis(appCode));
        boolean threads = Boolean.parseBoolean(params.getOrDefault(threadsKey, String.valueOf(profilerSettingsStore.isThreads(appCode))));
        String event = params.getOrDefault(eventKey, profilerSettingsStore.getEvent(appCode));
        String modeCode = params.getOrDefault(eventKey, profilerSettingsStore.getModeCode(appCode));

        List<String> chunk = Lists.newArrayListWithExpectedSize(6);
        chunk.add(BistouryConstants.REQ_PROFILER_START);
        chunk.add(BistouryConstants.PROFILER_ID);
        chunk.add(durationKey);
        chunk.add(duration);
        chunk.add(frequencyKey);
        chunk.add(frequency);
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
        return new ProfilerSettings(appCode, Integer.parseInt(duration), Integer.parseInt(frequency),
                modeCode == null ? Profiler.Mode.async_sampler.code : Integer.parseInt(modeCode),
                SPACE_JOINER.join(chunk));
    }

    private Map<String, String> getCommandParams(List<String> paramChunk) {
        Map<String, String> params = Maps.newHashMapWithExpectedSize(paramChunk.size() / 2);
        for (int i = 0; i < paramChunk.size() / 2; i++) {
            String paramKey = paramChunk.get(2 * i);
            String value = paramChunk.get(2 * i + 1);
            params.put(paramKey, value);
        }
        return params;
    }
}
