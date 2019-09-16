package qunar.tc.bistoury.serverside.jdbc;

import javax.sql.DataSource;

import com.google.common.base.Strings;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;

/**
 * @author xkrivzooh
 * @since 2019/8/15
 */
public class DefaultDataSourceFactory implements DataSourceFactory {

    @Override
    public DataSource createDataSource(DynamicConfig dynamicConfig) {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(dynamicConfig.getString("jdbc.driverClassName", "com.mysql.jdbc.Driver"));
        String priorityUrl = System.getProperty("bistoury.jdbc.url");
        dataSource.setUrl(Strings.isNullOrEmpty(priorityUrl) ? dynamicConfig.getString("jdbc.url") : priorityUrl);
        dataSource.setUsername(dynamicConfig.getString("jdbc.username"));
        dataSource.setPassword(dynamicConfig.getString("jdbc.password"));
        dataSource.setMaxActive(30);
        dataSource.setMinIdle(20);
        dataSource.setMaxWait(3000);
        dataSource.setValidationQuery("select 1");
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        return dataSource;
    }
}
