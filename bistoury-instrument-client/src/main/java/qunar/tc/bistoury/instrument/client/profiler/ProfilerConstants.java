package qunar.tc.bistoury.instrument.client.profiler;

import java.io.File;

/**
 * @author cai.wen created on 2019/10/23 11:38
 */
public class ProfilerConstants {

    public static final String FREQUENCY = "frequency";

    public static final String DURATION = "duration";

    public static final String EVENT = "event";

    public static final String THREADS = "threads";

    public static final String TMP_DIR = "tmpDir";

    //30 seconds
    public static final long MIN_DURATION_MILLIS = 30000;

    //30 minutes
    public static final long MAX_DURATION_MILLIS = 1800000;

    public static final String PROFILER_ID = "profilerId";

    //agent和target的临时目录,不一定相同
    public static volatile String PROFILER_ROOT_PATH = System.getProperty("java.io.tmpdir") + File.separator + "bistoury-profiler";

    public static volatile String PROFILER_TEMP_PATH = System.getProperty("java.io.tmpdir") + File.separator + "bistoury-profiler" + File.separator + "tmp";

}
