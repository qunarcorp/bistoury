package qunar.tc.bistoury.instrument.client.profiler.async;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.common.OsUtils;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerContext;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerInfo;
import qunar.tc.bistoury.instrument.client.profiler.ProfilerStore;
import qunar.tc.bistoury.instrument.client.profiler.Profilers;

import java.io.File;
import java.util.Map;

import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.EVENT;
import static qunar.tc.bistoury.instrument.client.profiler.ProfilerConstants.STORE_DIR;

/**
 * @author zhenyu.nie created on 2019 2019/12/31 14:53
 */
public class AsyncProfilerStore implements ProfilerStore {

    //async-profiler profiler.cpp:876
    private static final String NOT_ACTIVE_PREFIX = "Profiler is not active";

    private final ProfilerInfo profilerInfo;

    public AsyncProfilerStore(ProfilerInfo profilerInfo) {
        this.profilerInfo = profilerInfo;
    }

    @Override
    public ProfilerContext start(Map<String, String> config) {
        Map<String, String> params = Maps.newHashMap(config);
        if (!params.containsKey(EVENT)) {
            params.put(EVENT, getPlatformEvent());
        }
        if (params.containsKey(STORE_DIR)) {
            BistouryStore.changeProfilerStorePath(params.remove(STORE_DIR));
        }
        AsyncProfilerContext profilerContext = new AsyncProfilerContext(this, params, profilerInfo);
        profilerContext.start();
        return profilerContext;
    }

    void start(AsyncProfilerContext profilerContext) {
        String startCommand = profilerContext.createStartCommand();
        Manager.execute(startCommand);
    }

    void stop(AsyncProfilerContext profilerContext) {
        String stopCommand = profilerContext.createStopCommand();
        Manager.execute(stopCommand);
        renameProfilerPath(profilerContext.getId());
    }

    void tryStop(AsyncProfilerContext profilerContext) {
        String stopCommand = profilerContext.createStopCommand();
        Manager.tryStop(stopCommand);
        renameProfilerPath(profilerContext.getId());
    }

    private void renameProfilerPath(String profilerId) {
        Optional<File> profilerDirRef = ProfilerUtil.getProfilerDir(BistouryStore.getProfilerTempPath(), profilerId);
        if (profilerDirRef.isPresent()) {
            File tmpDir = profilerDirRef.get();
            final String profilerTempPath = BistouryStore.getProfilerRootPath();
            new File(profilerTempPath).mkdirs();
            File realProfilerDir = new File(profilerTempPath, tmpDir.getName());
            tmpDir.renameTo(realProfilerDir);
        }
    }

    @Override
    public boolean isRunning() {
        return ProfilerUtil.RUNNING_STATUS.equals(getStatus());
    }

    @Override
    public String getStatus() {
        String statusResult = Manager.execute("status");
        if (statusResult.startsWith(NOT_ACTIVE_PREFIX)) {
            return ProfilerUtil.FINISH_STATUS;
        } else {
            return ProfilerUtil.RUNNING_STATUS;
        }
    }

    @Override
    public String getStatus(String id) {
        return Profilers.findNotRunningStatus(id);
    }

    @Override
    public void clear() {
        Manager.clear();
    }

    private static String getPlatformEvent() {
        // proxy端不指定event时,启用
        if (OsUtils.isSupportPerf()) {
            return "cpu";
        }
        return "itimer";
    }
}
