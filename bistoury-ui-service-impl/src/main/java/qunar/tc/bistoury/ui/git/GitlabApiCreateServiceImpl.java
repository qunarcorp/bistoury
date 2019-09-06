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

import org.gitlab.api.GitlabAPI;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;
import qunar.tc.bistoury.ui.model.PrivateToken;
import qunar.tc.bistoury.ui.security.LoginContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author keli.wang
 */
@Service
public class GitlabApiCreateServiceImpl implements GitlabApiCreateService {

    private String gitlabEndpoint;

    @PostConstruct
    public void init() {
        DynamicConfig<LocalDynamicConfig> config = DynamicConfigLoader.load("config.properties");
        config.addListener(conf -> gitlabEndpoint = conf.getString("gitlab.endpoint"));
    }

    @Resource
    private GitPrivateTokenService gitPrivateTokenService;

    @Override
    public GitlabAPI create() {
        final String userCode = LoginContext.getLoginContext().getLoginUser();
        return createForUser(userCode);
    }

    @Override
    public GitlabAPI createForUser(final String userCode) {
        Optional<PrivateToken> token = gitPrivateTokenService.queryToken(userCode);
        if (!token.isPresent()) {
            throw new RuntimeException("尚未设置 Gitlab Private Token");
        }
        return GitlabAPI.connect(gitlabEndpoint, token.get().getPrivateToken());
    }
}
