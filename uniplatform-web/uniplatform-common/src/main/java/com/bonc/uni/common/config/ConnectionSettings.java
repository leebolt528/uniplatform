package com.bonc.uni.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="mysql")
public class ConnectionSettings {
	private String driver;
	private String url;
	private String username;
	private String password;
	private boolean showsql;
	
	public ConnectionSettings() {}
	
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isShowsql() {
		return showsql;
	}

	public void setShowsql(boolean showsql) {
		this.showsql = showsql;
	}
	
	
}
