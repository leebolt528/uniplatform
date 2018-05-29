package com.bonc.uni.nlp.utils.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.bonc.usdp.odk.logmanager.LogManager;

public class DBConnectionPool {

	private static DBConnectionPool instance;
	
	private DataSource dataSource;
	
	private DBConnectionPool() {
		try {
			dataSource = DruidDataSourceFactory.createDataSource(DataBaseConfig.dataBaseProp);
		} catch (Exception e) {
			LogManager.Exception(e);
		}
	}

	public synchronized static DBConnectionPool getInstance() {
		if(null == instance) {
			instance = new DBConnectionPool();
		}
		return instance;
	}
	
	public Connection getConnection() {
		if(null == dataSource) {
			return null;
		}
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			LogManager.Exception(e);
		}
		
		return conn;
	}
	
}
