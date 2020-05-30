package qunar.tc.bistoury.agent.common.kv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author leix.xie
 * @date 2020/3/18 11:31
 * @describe
 */
public class DataSourceHelper {
    private static final Logger LOG = LoggerFactory.getLogger(DataSourceHelper.class);

    public static void closeResource(ResultSet resultSet, Statement statement, Connection connection) {
        // 关闭结果集
        // ctrl+alt+m 将java语句抽取成方法
        closeResource(resultSet);
        // 关闭语句执行者
        closeResource(statement);
        // 关闭连接
        closeResource(connection);
    }

    public static void closeResource(ResultSet resultSet, Statement statement) {
        // 关闭结果集
        // ctrl+alt+m 将java语句抽取成方法
        closeResource(resultSet);
        // 关闭语句执行者
        closeResource(statement);
    }

    public static void closeResource(Statement statement, Connection connection) {
        // 关闭语句执行者
        closeResource(statement);
        // 关闭连接
        closeResource(connection);
    }

    public static void closeResource(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("close connection error", e);
            }
        }
    }

    public static void closeResource(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOG.error("close statement error", e);
            }
        }
    }

    public static void closeResource(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOG.error("close ResultSet error", e);
            }
        }
    }
}
