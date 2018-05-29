package com.bonc.uni.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.bonc.uni.common.service.SendService;

@Service("emailService")
public class EmailSendServiceImpl implements SendService{

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")  
    private String from;
	
	@Override
	public void push(String[] receivers, String subject, String msg) {
		try {
			if(null != javaMailSender) {
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(from);
				message.setTo(receivers);
				message.setSubject(subject);
				message.setText(msg);
				javaMailSender.send(message);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
