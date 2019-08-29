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

package qunar.tc.bistoury.application.mysql.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import qunar.tc.bistoury.application.mysql.dao.ApplicationUserDao;

/**
 * @author leix.xie
 * @date 2019/7/2 11:14
 * @describe
 */
@Repository
public class ApplicationUserDaoImpl implements ApplicationUserDao {
    private static final String SELECT_APP_BY_USER_CODE = "select app_code from bistoury_user_app where user_code=?";

    private static final String ADD_USER_FOR_APP = "insert ignore into bistoury_user_app (app_code, user_code) values (?, ?)";

    private static final String REMOVE_USER_FROM_APP = "delete from bistoury_user_app where user_code = ? and app_code = ?";

    private static final String SELECT_USER_BY_APP = "select user_code from bistoury_user_app where app_code = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<String> getAppCodesByUserCode(final String userCode) {
        return jdbcTemplate.query(SELECT_APP_BY_USER_CODE, LIST_APPCODE, userCode);
    }

    @Override
    public List<String> getUsersByAppCode(final String appCode) {
        return jdbcTemplate.query(SELECT_USER_BY_APP, LIST_USER, appCode);
    }

    @Override
    public int addAppUser(final String userCode, final String appCode) {
        return jdbcTemplate.update(ADD_USER_FOR_APP, appCode, userCode);
    }

    @Override
    public void batchAddAppUser(final List<String> userCodes, final String addCode) {
        jdbcTemplate.batchUpdate(ADD_USER_FOR_APP, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setString(1, addCode);
                preparedStatement.setString(2, userCodes.get(i));
            }

            @Override
            public int getBatchSize() {
                return userCodes.size();
            }
        });
    }

    @Override
    public int removeAppUser(final String userCode, final String appCode) {
        return jdbcTemplate.update(REMOVE_USER_FROM_APP, userCode, appCode);
    }

    private static final RowMapper<String> LIST_APPCODE = (rs, rowNum) -> rs.getString("app_code");

    private static final RowMapper<String> LIST_USER = (rs, rowNum) -> rs.getString("user_code");

}
