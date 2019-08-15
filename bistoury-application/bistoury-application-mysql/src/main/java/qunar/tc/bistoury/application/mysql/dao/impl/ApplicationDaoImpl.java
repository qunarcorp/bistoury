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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import qunar.tc.bistoury.application.api.pojo.Application;
import qunar.tc.bistoury.application.mysql.dao.ApplicationDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019/7/2 10:47
 * @describe
 */
@Repository
public class ApplicationDaoImpl implements ApplicationDao {
    private static final String SELECT_BY_APP_CODE = "select id, code, name, group_code, status, creator, create_time from bistoury_app where bistoury_app.code=?";

    private static final String SELECT_BY_APP_CODES = "SELECT id, code, name, group_code, status, creator, create_time FROM bistoury_app WHERE code in (:code)";

    private static final String SELECT_ALL_APPS = "select id, code, name, group_code, status, creator, create_time from bistoury_app";

    private static final String INSERT_APP = "INSERT INTO bistoury_app (code, name, group_code, status, creator) VALUES (?, ?, ?, ?, ?)";

    private static final String CHECK_EXIST_SQL = "SELECT code FROM bistoury_app WHERE code = ?";

    private static final String UPDATE_APP = "UPDATE bistoury_app SET name = ?, group_code=?,status=? WHERE code = ? ";
    private static ResultSetExtractor<Application> APPLICATION_MAPPER = resultSet -> {
        if (resultSet.next()) {
            return getApplicationFromRs(resultSet);
        }
        return null;
    };
    private static RowMapper<Application> APPLICATIONS_MAPPER = (resultSet, i) -> getApplicationFromRs(resultSet);
    private static ResultSetExtractor<String> CODE_MAPPER = resultSet -> {
        if (resultSet.next()) {
            return resultSet.getString("code");
        }
        return null;
    };
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static Application getApplicationFromRs(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String code = resultSet.getString("code");
        String name = resultSet.getString("name");
        String groupCode = resultSet.getString("group_code");
        int status = resultSet.getInt("status");
        String creator = resultSet.getString("creator");
        Timestamp createTime = resultSet.getTimestamp("create_time");
        return new Application(id, code, name, groupCode, status, creator, createTime);
    }

    @Override
    public int createApplication(Application application) {
        return jdbcTemplate.update(INSERT_APP, application.getCode(), application.getName(), application.getGroupCode(), application.getStatus(), application.getCreator());
    }

    @Override
    public int updateApplication(Application application) {
        return jdbcTemplate.update(UPDATE_APP, application.getName(), application.getGroupCode(), application.getStatus(), application.getCode());
    }

    @Override
    public Application getApplicationByAppCode(final String appCode) {
        return jdbcTemplate.query(SELECT_BY_APP_CODE, APPLICATION_MAPPER, appCode);
    }

    @Override
    public List<Application> getApplicationsByAppCodes(List<String> appCodes) {
        if (CollectionUtils.isEmpty(appCodes)) {
            return Collections.emptyList();
        }
        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("code", appCodes);
        return namedParameterJdbcTemplate.query(SELECT_BY_APP_CODES, parameters, APPLICATIONS_MAPPER);
    }

    @Override
    public List<Application> getAllApplications() {
        return this.jdbcTemplate.query(SELECT_ALL_APPS, APPLICATIONS_MAPPER);
    }

    @Override
    public boolean checkExist(String appCode) {
        String result = jdbcTemplate.query(CHECK_EXIST_SQL, CODE_MAPPER, appCode);
        return result != null;
    }
}
