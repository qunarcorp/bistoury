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

package qunar.tc.bistoury.ui.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import qunar.tc.bistoury.ui.dao.GitlabPrivateTokenDao;
import qunar.tc.bistoury.ui.model.PrivateToken;

import java.util.Date;

/**
 * @author keli.wang
 */
@Repository
public class GitlabPrivateTokenDaoImpl implements GitlabPrivateTokenDao {
    private static final String SAVE_TOKEN_SQL = "INSERT INTO bistoury_gitlab_token(`user_code`, `private_token`, `create_time`) VALUES (?, ?,?) ON DUPLICATE KEY UPDATE private_token=?";
    private static final String QUERY_TOKEN_SQL = "SELECT user_code, private_token FROM `bistoury_gitlab_token` WHERE `user_code`=?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int saveToken(String userCode, String privateToken) {
        return jdbcTemplate.update(SAVE_TOKEN_SQL, userCode, privateToken, new Date(), privateToken);
    }

    @Override
    public PrivateToken queryToken(String userCode) {
        return jdbcTemplate.query(QUERY_TOKEN_SQL, rs -> {
            if (rs.next()) {
                PrivateToken privateToken = new PrivateToken();
                final String user_code = rs.getString("user_code");
                final String private_token = rs.getString("private_token");
                privateToken.setUserCode(user_code);
                privateToken.setPrivateToken(private_token);
                return privateToken;
            }
            return null;
        }, userCode);
    }
}
