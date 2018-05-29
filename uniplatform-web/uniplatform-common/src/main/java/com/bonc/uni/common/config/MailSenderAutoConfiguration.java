package com.bonc.uni.common.config;

import java.util.Map;
import java.util.Properties;

import javax.activation.MimeType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.bonc.uni.common.config.MailSenderAutoConfiguration.MailSenderCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


/**
 * 获取邮件服务
 * @author futao
 * 2017年9月4日
 */
@Configuration
@ConditionalOnClass({ MimeMessage.class, MimeType.class })
@ConditionalOnMissingBean(MailSender.class)
@Conditional(MailSenderCondition.class)
@EnableConfigurationProperties(MailProperties.class)
//@Import(JndiSessionConfiguration.class)
public class MailSenderAutoConfiguration {

	private final MailProperties properties;

	private final Session session;

	public MailSenderAutoConfiguration(MailProperties properties,
			ObjectProvider<Session> session) {
		this.properties = properties;
		this.session = session.getIfAvailable();
	}

	@Bean
	public JavaMailSenderImpl mailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		if (this.session != null) {
			javaMailSender.setSession(this.session);
		}
		else {
			applyProperties(javaMailSender);
		}
		boolean validate = validateConnection(javaMailSender);
		if(false == validate) {
			System.out.println("请重新配置邮件.................");
			return null;
		}
		return javaMailSender;
	}

	private void applyProperties(JavaMailSenderImpl sender) {
		sender.setHost(this.properties.getHost());
		if (this.properties.getPort() != null) {
			sender.setPort(this.properties.getPort());
		}
		sender.setUsername(this.properties.getUsername());
		sender.setPassword(this.properties.getPassword());
		sender.setProtocol(this.properties.getProtocol());
		if (this.properties.getDefaultEncoding() != null) {
			sender.setDefaultEncoding(this.properties.getDefaultEncoding().name());
		}
		if (!this.properties.getProperties().isEmpty()) {
			sender.setJavaMailProperties(asProperties(this.properties.getProperties()));
		}
	}

	private Properties asProperties(Map<String, String> source) {
		Properties properties = new Properties();
		properties.putAll(source);
		return properties;
	}
	
	public boolean validateConnection(JavaMailSenderImpl javaMailSender) {
		try {
			javaMailSender.testConnection();
		}
		catch (MessagingException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Condition to trigger the creation of a {@link JavaMailSenderImpl}. This kicks in if
	 * either the host or jndi name property is set.
	 */
	static class MailSenderCondition extends AnyNestedCondition {

		MailSenderCondition() {
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}

		@ConditionalOnProperty(prefix = "spring.mail", name = "host")
		static class HostProperty {

		}

		@ConditionalOnProperty(prefix = "spring.mail", name = "jndi-name")
		static class JndiNameProperty {

		}

	}

}