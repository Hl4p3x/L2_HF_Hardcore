/*
 * Copyright (C) 2004-2016 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.common.pool.impl;

import com.l2jserver.common.config.CommonConfig;
import com.l2jserver.common.pool.IConnectionFactory;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

/**
 * HikariCP Connection Factory implementation.<br>
 * <b>Note that this class is not public to prevent external initialization.</b><br>
 * <b>Access it through {@link ConnectionFactory} and proper configuration.</b>
 *
 * @author Zoey76
 */
enum HikariCPConnectionFactory implements IConnectionFactory {
	INSTANCE;

	private final HikariDataSource _dataSource;

	HikariCPConnectionFactory() {
		_dataSource = new HikariDataSource();
		_dataSource.setJdbcUrl(CommonConfig.DATABASE_PROPERTIES.url);
		_dataSource.setUsername(CommonConfig.DATABASE_PROPERTIES.login);
		_dataSource.setPassword(CommonConfig.DATABASE_PROPERTIES.password);
		_dataSource.setMaximumPoolSize(CommonConfig.DATABASE_PROPERTIES.maximumConnections);
		_dataSource.setIdleTimeout(CommonConfig.DATABASE_PROPERTIES.maximumIdleTime);
		_dataSource.setDriverClassName(CommonConfig.DATABASE_PROPERTIES.driver);
		_dataSource.setConnectionTimeout(CommonConfig.DATABASE_PROPERTIES.connectionTimeout);
	}
	
	@Override
	public void close()
	{
		try
		{
			_dataSource.close();
		}
		catch (Exception e)
		{
			LOG.warn("There has been a problem closing the data source!", e);
		}
	}
	
	@Override
	public DataSource getDataSource()
	{
		return _dataSource;
	}

}
