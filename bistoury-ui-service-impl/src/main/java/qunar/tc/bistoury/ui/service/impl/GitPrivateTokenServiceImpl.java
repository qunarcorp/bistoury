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

package qunar.tc.bistoury.ui.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.ui.dao.GitlabPrivateTokenDao;
import qunar.tc.bistoury.ui.model.PrivateToken;
import qunar.tc.bistoury.ui.service.AESCryptService;
import qunar.tc.bistoury.ui.service.GitPrivateTokenService;

import java.util.Optional;

/**
 * @author keli.wang
 */
@Service
public class GitPrivateTokenServiceImpl implements GitPrivateTokenService {

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
