package qunar.tc.bistoury.ui.service.impl;

import org.gitlab.api.GitlabAPI;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.ui.model.PrivateToken;
import qunar.tc.bistoury.ui.security.LoginContext;
import qunar.tc.bistoury.ui.service.GitlabApiCreateService;
import qunar.tc.bistoury.ui.service.GitlabPrivateTokenService;

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
        DynamicConfig config = DynamicConfigLoader.load("config.properties");
        config.addListener(conf -> gitlabEndpoint = conf.getString("gitlab.endpoint"));
    }

    @Resource
    private GitlabPrivateTokenService gitlabPrivateTokenService;

    @Override
    public GitlabAPI create() {
        final String userCode = LoginContext.getLoginContext().getLoginUser();
        return createForUser(userCode);
    }

    @Override
    public GitlabAPI createForUser(final String userCode) {
        Optional<PrivateToken> token = gitlabPrivateTokenService.queryToken(userCode);
        if (!token.isPresent()) {
            throw new RuntimeException("尚未设置 Gitlab Private Token");
        }
        return GitlabAPI.connect(gitlabEndpoint, token.get().getPrivateToken());
    }
}
