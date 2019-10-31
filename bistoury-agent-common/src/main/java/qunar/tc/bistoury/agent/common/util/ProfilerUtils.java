package qunar.tc.bistoury.agent.common.util;

import qunar.tc.bistoury.common.BistouryConstants;

import java.io.File;

/**
 * @author cai.wen created on 2019/10/28 11:59
 */
public class ProfilerUtils {

    public static boolean isDone(final String profilerId) {
        return new File(BistouryConstants.PROFILER_ROOT_PATH, profilerId).exists();
    }
}
