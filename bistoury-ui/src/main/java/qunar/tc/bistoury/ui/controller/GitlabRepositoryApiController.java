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

package qunar.tc.bistoury.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.ui.git.GitRepositoryApi;
import qunar.tc.bistoury.ui.git.GitRepositoryApiStore;

import java.io.IOException;

/**
 * @author keli.wang
 */
@Controller
@RequestMapping("/api/gitlab/repository")
public class GitlabRepositoryApiController {

    @Autowired
    private GitRepositoryApiStore gitRepositoryApiStore;

    @RequestMapping("/tree")
    @ResponseBody
    public ApiResult tree(@RequestParam final String projectId,
                          @RequestParam final String path,
                          @RequestParam final String ref) throws IOException {
        GitRepositoryApi gitRepositoryApi = gitRepositoryApiStore.getGitRepositoryApi();
        return gitRepositoryApi.tree(projectId, path, ref);
    }

    @RequestMapping("/file")
    @ResponseBody
    public ApiResult file(@RequestParam final String projectId,
                          @RequestParam final String ref,
                          @RequestParam final String filepath) throws IOException {
        GitRepositoryApi gitRepositoryApi = gitRepositoryApiStore.getGitRepositoryApi();
        return gitRepositoryApi.file(projectId, ref, filepath);
    }

    @RequestMapping("/filebyclass")
    @ResponseBody
    public ApiResult file(@RequestParam final String projectId,
                          @RequestParam final String ref,
                          @RequestParam(required = false) final String module,
                          @RequestParam final String className) throws IOException {
        GitRepositoryApi gitRepositoryApi = gitRepositoryApiStore.getGitRepositoryApi();
        return gitRepositoryApi.fileByClass(projectId, ref, module, className);
    }
}
