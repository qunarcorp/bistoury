package qunar.tc.bistoury.ui.dao;

import qunar.tc.bistoury.ui.model.PrivateToken;

/**
 * @author keli.wang
 */
public interface GitlabPrivateTokenDao {
    int saveToken(String userId, String privateToken);

    PrivateToken queryToken(final String userId);
}
