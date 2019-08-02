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

package qunar.tc.bistoury.attach.file;

import com.google.common.base.Strings;

import java.util.Set;

/**
 * @author leix.xie
 * @date 2019-07-26 11:38
 * @describe
 */
public abstract class AbstractFileService implements FileService {
    public boolean isExclusionFile(final Set<String> exclusionFileSuffix, Set<String> exclusionFile, String fileName) {
        if (Strings.isNullOrEmpty(fileName)) {
            return false;
        }

        if (exclusionFile.contains(fileName)) {
            return true;
        }

        for (String fileSuffix : exclusionFileSuffix) {
            if (fileName.endsWith(fileSuffix)) {
                return true;
            }
        }
        return false;
    }
}
