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

package qunar.tc.bistoury.agent.common;

import com.google.common.base.Strings;

/**
 * @author zhenyu.nie created on 2019 2019/7/16 16:56
 */
public class ClosableProcesses {

    public static ClosableProcess wrap(Process process) {
        if (isUnixProcess()) {
            return new UnixProcess(process);
        } else {
            return new NormalProcess(process);
        }
    }

    private static boolean isUnixProcess() {
        String osName = Strings.nullToEmpty(getPlatform());
        return osName.equals("Linux") || osName.contains("OS X") || osName.equals("SunOS") || osName.equals("AIX");
    }

    private static String getPlatform() {
        return System.getProperty("os.name");
    }
}
