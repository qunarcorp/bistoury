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
import org.springframework.stereotype.Repository;

import qunar.tc.bistoury.application.api.pojo.AppServer;
import qunar.tc.bistoury.application.mysql.dao.AppServerDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019/7/2 14:18
 * @describe
 */
@Repository
public class AppServerDaoImpl implements AppServerDao {
    private static final String SELECT_APP_SERVER_BY_APPCODE = "select server_id,ip,port,host,log_dir,room,app_code,auto_jstack_enable,auto_jmap_histo_enable from bistoury_server where app_code=?";

    private static final String SELECT_APP_SERVER_BY_IP = "select server_id,ip,port,host,log_dir,room,app_code,auto_jstack_enable,auto_jmap_histo_enable from bistoury_server where ip=?";

    private static final String SELECT_APP_SERVER_BY_SERVER_ID = "select server_id,ip,port,host,log_dir,room,app_code,auto_jstack_enable,auto_jmap_histo_enable from bistoury_server where server_id=?";

    private static final String INSERT_APP_SERVER = "insert ignore into bistoury_server (server_id,ip,port,host,log_dir,room,app_code,auto_jstack_enable,auto_jmap_histo_enable) values (?,?,?,?,?,?,?,?,?);";

    private static final String DELETE_APP_SERVER_BY_IP_PORT = "delete from bistoury_server where ip=?";

    private static final String DELETE_APP_SERVER_BY_SERVER_ID = "delete from bistoury_server where server_id=?";

    private static final String UPDATE_APP_SERVER_BY_SERVER_ID = "update bistoury_server set ip=?, port=?,host=?,log_dir=?,room=? where server_id=?";

    private static final String CHANGE_AUTO_JMAP_HISTO_ENABLE = "update bistoury_server set auto_jmap_histo_enable=? where server_id=?";

    private static final String CHANGE_AUTO_JSTACK_ENABLE = "update bistoury_server set auto_jstack_enable=? where server_id=?";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<AppServer> getAppServerByAppCode(final String appCode) {
        return this.jdbcTemplate.query(SELECT_APP_SERVER_BY_APPCODE, APPLICATION_SERVER_ROW_MAPPER, appCode);
    }

    @Override
    public AppServer getAppServerByIp(final String ip) {
        return this.jdbcTemplate.query(SELECT_APP_SERVER_BY_IP, APPLICATION_SERVER_MAPPER, ip);
    }

    @Override
    public AppServer getAppServerByServerId(final String serverId) {
        return this.jdbcTemplate.query(SELECT_APP_SERVER_BY_SERVER_ID, APPLICATION_SERVER_MAPPER, serverId);
    }

    @Override
    public int addAppServer(AppServer appServer) {
        return this.jdbcTemplate.update(INSERT_APP_SERVER, appServer.getServerId(), appServer.getIp(), appServer.getPort(), appServer.getHost(), appServer.getLogDir(), appServer.getRoom(), appServer.getAppCode(), appServer.isAutoJStackEnable(), appServer.isAutoJMapHistoEnable());
    }

    @Override
    public int updateAppServer(AppServer appServer) {
        return this.jdbcTemplate.update(UPDATE_APP_SERVER_BY_SERVER_ID, appServer.getIp(), appServer.getPort(), appServer.getHost(), appServer.getLogDir(), appServer.getRoom(), appServer.getServerId());
    }

    @Override
    public int changeAutoJMapHistoEnable(final String serverId, final boolean enable) {
        return this.jdbcTemplate.update(CHANGE_AUTO_JMAP_HISTO_ENABLE, enable, serverId);
    }

    @Override
    public int changeAutoJStackEnable(final String serverId, final boolean enable) {
        return this.jdbcTemplate.update(CHANGE_AUTO_JSTACK_ENABLE, enable, serverId);
    }

    @Override
    public int deleteAppServerByIp(final String ip) {
        return this.jdbcTemplate.update(DELETE_APP_SERVER_BY_IP_PORT, ip);
    }

    @Override
    public int deleteAppServerByServerId(final String serverId) {
        return this.jdbcTemplate.update(DELETE_APP_SERVER_BY_SERVER_ID, serverId);
    }

    private static final ResultSetExtractor<AppServer> APPLICATION_SERVER_MAPPER = resultSet -> {
        if (resultSet.next()) {
            return getApplicationServerFromRs(resultSet);
        }
        return null;
    };
    private static final RowMapper<AppServer> APPLICATION_SERVER_ROW_MAPPER = (resultSet, i) -> getApplicationServerFromRs(resultSet);

    private static AppServer getApplicationServerFromRs(ResultSet resultSet) throws SQLException {
        String server_id = resultSet.getString("server_id");
        String ip = resultSet.getString("ip");
        int port = resultSet.getInt("port");
        String host = resultSet.getString("host");
        String log_dir = resultSet.getString("log_dir");
        String room = resultSet.getString("room");
        String app_code = resultSet.getString("app_code");
        boolean autoJstackEnable = resultSet.getBoolean("auto_jstack_enable");
        boolean autoJmapHistoEnable = resultSet.getBoolean("auto_jmap_histo_enable");
        return new AppServer(server_id, ip, port, host, log_dir, room, app_code, autoJstackEnable, autoJmapHistoEnable);
    }

}
