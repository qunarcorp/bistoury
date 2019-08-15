package qunar.tc.bistoury.serverside.jdbc;

import javax.sql.DataSource;

import qunar.tc.bistoury.serverside.configuration.DynamicConfig;

public interface DataSourceFactory {

	DataSource createDataSource(DynamicConfig dynamicConfig);

}
