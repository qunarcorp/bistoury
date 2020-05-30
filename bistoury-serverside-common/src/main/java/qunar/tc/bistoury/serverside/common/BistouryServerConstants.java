package qunar.tc.bistoury.serverside.common;

import java.io.File;

/**
 * @author cai.wen created on 19-12-27 上午10:59
 */
public class BistouryServerConstants {

    public static final String PROFILER_ROOT_PATH = System.getProperty("java.io.tmpdir") + File.separator + "bistoury-profiler";

    public static final String PROFILER_ROOT_TEMP_PATH = PROFILER_ROOT_PATH + File.separator + "tmp";

    public static final String PROFILER_ROOT_AGENT_PATH = PROFILER_ROOT_PATH + File.separator + "agent";
}
