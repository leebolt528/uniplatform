package com.bonc.uni.common.config;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 邮件配置文件
 * @author futao
 * 2017年9月4日
 */
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * SMTP server host.
	 */
	private String host;

	/**
	 * SMTP server port.
	 */
	private Integer port;

	/**
	 * Login user of the SMTP server.
	 */
	private String username;

	/**
	 * Login password of the SMTP server.
	 */
	private String password;

	/**
	 * Protocol used by the SMTP server.
	 */
	private String protocol = "smtp";

	/**
	 * Default MimeMessage encoding.
	 */
	private Charset defaultEncoding = DEFAULT_CHARSET;

	/**
	 * Additional JavaMail session properties.
	 */
	private Map<String, String> properties = new HashMap<String, String>();

	/**
	 * Session JNDI name. When set, takes precedence to others mail settings.
	 */
	private String jndiName;

	/**
	 * Test that the mail server is available on startup.
	 */
	private boolean testConnection;

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProtocol() {
		return this.protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Charset getDefaultEncoding() {
		return this.defaultEncoding;
	}

	public void setDefaultEncoding(Charset defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public Map<String, String> getProperties() {
		return this.properties;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getJndiName() {
		return this.jndiName;
	}

	public boolean isTestConnection() {
		return this.testConnection;
	}

	public void setTestConnection(boolean testConnection) {
		this.testConnection = testConnection;
	}

}