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

package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.ui.model.MavenInfo;

/**
 * @author: leix.xie
 * @date: 2019/4/3 10:56
 * @describe：
 */
public interface MavenRepositoryService {
    /**
     * 尝试从磁盘获取源文件内容，不存在时抛出SourceFileNotFoundException异常
     *
     * @param mavenInfo
     * @param className
     * @return
     */
    String getSourceFile(MavenInfo mavenInfo, String className);

    /**
     * 从maven私服获取源文件jar包，然后读取文件内容
     *
     * @param mavenInfo
     * @param className
     * @return
     */
    String downSourceFile(MavenInfo mavenInfo, String className);
}
