package qunar.tc.bistoury.ui.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.ui.dao.GitlabPrivateTokenDao;
import qunar.tc.bistoury.ui.model.PrivateToken;
import qunar.tc.bistoury.ui.service.AESCryptService;
import qunar.tc.bistoury.ui.service.GitlabPrivateTokenService;

import java.util.Optional;

/**
 * @author keli.wang
 */
@Service
public class GitlabPrivateTokenServiceImpl implements GitlabPrivateTokenService {

    @Autowired
    private GitlabPrivateTokenDao gitlabPrivateTokenDao;

    @Autowired
    private AESCryptService aesCryptService;

    @Override
    public int saveToken(String username, String privateToken) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(username), "用户Code不能为空");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(privateToken), " token 不能为空");
        final String token = aesCryptService.encrypt(privateToken);
        return gitlabPrivateTokenDao.saveToken(username, token);
    }

    @Override
    public Optional<PrivateToken> queryToken(String userCode) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(userCode), "用户Code不能为空");
        final PrivateToken privateToken = gitlabPrivateTokenDao.queryToken(userCode);
        if (privateToken == null) {
            return Optional.empty();
        }
        String token = privateToken.getPrivateToken();
        if (Strings.isNullOrEmpty(token)) {
            return Optional.empty();
        }
        privateToken.setPrivateToken(aesCryptService.decrypt(token));
        return Optional.of(privateToken);
    }
}
