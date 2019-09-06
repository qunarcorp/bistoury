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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;

import javax.annotation.PostConstruct;

/**
 * @author leix.xie
 * @date 2019/9/4 15:21
 * @describe
 */
@Component
public class GitRepositoryApiStore {

    @Autowired
    private GitRepositoryApi gitlabRepositoryApiImpl;

    @Autowired
    private GitRepositoryApi githubRepositoryApiImpl;

    private DynamicConfig<LocalDynamicConfig> config;

    private static final String GITLAB = "gitlab";
    private static final String GITHUB = "github";
    private static final String GIT_KEY = "git.repository";
    private static String gitRepository;

    @PostConstruct
    public void init() {
        DynamicConfig<LocalDynamicConfig> dynamicConfig = DynamicConfigLoader.load("config.properties");
        dynamicConfig.addListener(conf -> gitRepository = conf.getString(GIT_KEY));
    }

    public final GitRepositoryApi getGitRepositoryApi() {
        if (GITHUB.equalsIgnoreCase(gitRepository)) {
            return githubRepositoryApiImpl;
        } else if (GITLAB.equalsIgnoreCase(gitRepository)) {
            return gitlabRepositoryApiImpl;
        }
        throw new RuntimeException("");
    }
}
