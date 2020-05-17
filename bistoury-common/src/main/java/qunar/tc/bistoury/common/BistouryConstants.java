/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.common;

import java.io.File;

/**
 * @author zhenyu.nie created on 2018 2018/11/28 19:59
 */
public class BistouryConstants {

    public static final String MAX_RUNNING_MS = "max.running.ms";

    public static final String FILL_PID = "$$FILLPID$$";

    public static final String FILL_DUMP_TARGET = "$$DUMPTARGET$$";

    public static final String PID_PARAM = " -pid";

    public static final String REQ_JAR_DEBUG = "jardebug";

    public static final String REQ_JAR_CLASS_PATH = "jarclasspath";

    public static final String REQ_DEBUG_ADD = "qdebugadd";

    public static final String REQ_DEBUG_REMOVE = "qdebugremove";

    public static final String REQ_DEBUG_SEARCH = "qdebugsearch";

    //获取项目发布的分支或TAG
    public static final String REQ_DEBUG_RELEASE_INFO = "qdebugreleaseinfo";

    public static final String REQ_MONITOR_ADD = "qmonitoradd";

    public static final String REQ_MONITOR_SNAPSHOT = "qmonitorsnapshot";

    public static final String REQ_JAR_INFO = "jarinfo";

    public static final String REQ_APP_CONFIG = "appconfig";

    public static final String REQ_APP_CONFIG_FILE = "appconfigfile";

    public static final String REQ_AGENT_INFO = "agentinfopush";

    public static final String REQ_PROFILER_START = "profilerstart";

    public static final String REQ_PROFILER_STOP = "profilerstop";

    public static final String REQ_PROFILER_STATE_SEARCH = "profilerstatesearch";

    public static final String REQ_PROFILER_START_STATE_SEARCH = "profilerstartsearch";

    public static final String REQ_PROFILER_FINISH_STATE_SEARCH = "profilerfinishsearch";

    public static final String REQ_PROFILER_INFO = "profilerinfo";

    public static final String BISTOURY_COMMAND_THREAD_NAME = "bistoury-command-execute-daemon";

    public static final String SPY_CLASSNAME = "qunar.tc.bistoury.instrument.spy.BistourySpys1";

    public static final String PROFILER_ID = "$$profilerId$$";

    // todo: 先这么写吧
    public static final String CURRENT_VERSION = "2.0.7";

    public static final String BISTOURY_VERSION_LINE_PREFIX = "bistoury version:";

    public static final String SHUTDOWN_COMMAND = "shutdown";

    public static final String STOP_COMMAND = "stop";

    public static final int MIN_AGENT_VERSION_SUPPORT_JOB_PAUSE = 12;

    public static final String PROFILER_ROOT_PATH = System.getProperty("java.io.tmpdir") + File.separator + "bistoury-profiler";

    public static final String PROFILER_ROOT_TEMP_PATH = PROFILER_ROOT_PATH + File.separator + "tmp";

    public static final String PROFILER_ROOT_AGENT_PATH = PROFILER_ROOT_PATH + File.separator + "agent";

    public static final String PROFILER_DIR_HEADER = "profilerDir";

    public static final String PROFILER_NAME_HEADER = "profilerName";
}
