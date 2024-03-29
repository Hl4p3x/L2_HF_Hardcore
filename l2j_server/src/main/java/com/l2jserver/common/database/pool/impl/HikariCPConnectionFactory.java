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
package com.l2jserver.common.database.pool.impl;

import com.l2jserver.Config;
import com.l2jserver.common.database.pool.IConnectionFactory;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

/**
 * HikariCP Connection Factory implementation.<br>
 * <b>Note that this class is not public to prevent external initialization.</b><br>
 * <b>Access it through {@link ConnectionFactory} and proper configuration.</b>
 * @author Zoey76
 */
enum HikariCPConnectionFactory implements IConnectionFactory
{
	INSTANCE;
	
	private final HikariDataSource _dataSource;
	
	HikariCPConnectionFactory()
	{
		_dataSource = new HikariDataSource();
		_dataSource.setJdbcUrl(Config.DATABASE_URL);
		_dataSource.setUsername(Config.DATABASE_LOGIN);
		_dataSource.setPassword(Config.DATABASE_PASSWORD);
		_dataSource.setMaximumPoolSize(Config.DATABASE_MAX_CONNECTIONS);
		_dataSource.setIdleTimeout(Config.DATABASE_MAX_IDLE_TIME);
		_dataSource.setDriverClassName(Config.DATABASE_DRIVER);
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
