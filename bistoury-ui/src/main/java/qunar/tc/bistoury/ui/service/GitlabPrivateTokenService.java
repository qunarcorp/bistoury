package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.ui.model.PrivateToken;

import java.util.Optional;

/**
 * @author keli.wang
 */
public interface GitlabPrivateTokenService {
    int saveToken(final String username, final String privateToken);

    Optional<PrivateToken> queryToken(String userCode);
}
