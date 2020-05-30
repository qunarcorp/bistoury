package qunar.tc.bistoury.instrument.client.profiler;

import com.google.common.base.Optional;
import qunar.tc.bistoury.clientside.common.store.BistouryStore;
import qunar.tc.bistoury.common.ProfilerUtil;

import java.io.File;

/**
 * @author zhenyu.nie created on 2019 2019/11/27 10:32
 */
public class Profilers {

    public static String findNotRunningStatus(String id) {
        Optional<File> profilerDirRef = ProfilerUtil.getProfilerDir(BistouryStore.DEFAULT_PROFILER_ROOT_PATH, id);
        if (profilerDirRef.isPresent()) {
            return ProfilerUtil.FINISH_STATUS;
        } else {
            return ProfilerUtil.ERROR_STATUS;
        }
    }
}
