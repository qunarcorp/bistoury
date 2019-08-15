package qunar.tc.bistoury.serverside.jdbc;

import javax.sql.DataSource;

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
		dataSource.setUrl(dynamicConfig.getString("jdbc.url"));
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
