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

import org.springframework.stereotype.Component;
import qunar.tc.bistoury.serverside.bean.ApiResult;

/**
 * @author leix.xie
 * @date 2019/9/4 16:51
 * @describe
 */
@Component
public class GithubRepositoryApiImpl implements GitRepositoryApi {

    @Override
    public ApiResult tree(String projectId, String path, String ref) {
        return null;
    }

    @Override
    public ApiResult file(String projectId, String path, String ref) {
        return null;
    }

    @Override
    public ApiResult fileByClass(String projectId, String ref, String module, String className) {
        return null;
    }
}
