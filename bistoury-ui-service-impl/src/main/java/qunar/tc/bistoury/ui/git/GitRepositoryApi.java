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

package qunar.tc.bistoury.ui.git;

import qunar.tc.bistoury.serverside.bean.ApiResult;

import java.io.IOException;

/**
 * @author leix.xie
 * @date 2019/9/4 16:50
 * @describe
 */
public interface GitRepositoryApi {

    ApiResult file(final String projectId, final String path, final String ref) throws IOException;

    ApiResult fileByClass(final String projectId, final String ref, final String module, final String className) throws IOException;

    void destroy();
}
