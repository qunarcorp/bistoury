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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabBranch;
import org.gitlab.api.models.GitlabProject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.security.LoginContext;
import qunar.tc.bistoury.ui.service.GitlabApiCreateService;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author keli.wang
 */
@Controller
@RequestMapping("/api/gitlab/project")
public class GitlabProjectApiController {

    @Resource
    private GitlabApiCreateService gitlabApiCreateService;

    private final LoadingCache<String, List<String>> ownedProjectsCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(String userCode) throws Exception {
                    final GitlabAPI api = gitlabApiCreateService.createForUser(userCode);
                    return api.getProjects().stream()
                            .map(GitlabProject::getPathWithNamespace)
                            .collect(Collectors.toList());
                }
            });

    @RequestMapping("/owned")
    @ResponseBody
    public ApiResult owned() {
        try {
            String userCode = LoginContext.getLoginContext().getLoginUser();
            return ResultHelper.success(ownedProjectsCache.getUnchecked(userCode));
        } catch (Exception e) {
            return ResultHelper.fail(-1, e.getMessage());
        }
    }

    @RequestMapping("/branch")
    @ResponseBody
    public ApiResult branch(@RequestParam("project") String project) {
        try {
            final GitlabAPI api = gitlabApiCreateService.create();
            return ResultHelper.success(api.getBranches(project)
                    .stream()
                    .map(GitlabBranch::getName)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResultHelper.fail(-1, e.getMessage());
        }
    }
}
