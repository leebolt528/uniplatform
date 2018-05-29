package com.bonc.uni.common.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bonc.uni.portals.PortalsApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PortalsApplication.class)
public class MailTest {

	@Autowired
	private JavaMailSender mailSender;
	
	@Test
    public void test() {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom("18701313086@163.com");
			message.setTo("futao@bonc.com.cn");
			message.setSubject("主题：简单邮件");
			message.setText("测试邮件内容");
			mailSender.send(message);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
