package com.bonc.uni.common.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bonc.uni.common.dao.AlertRepository;
import com.bonc.uni.common.entity.Alert;
import com.bonc.uni.common.jpa.Specification.SpecificationUtil;
import com.bonc.uni.common.service.AlertService;
import com.bonc.uni.common.service.SendService;
import com.bonc.uni.common.util.AlertUtil;
import com.bonc.uni.common.util.PlatformType;

@Service("alertService")
public class AlertServiceImpl implements AlertService {

	@Autowired
	AlertRepository alertRepository;
	
	@Autowired
	SendService emailService;
	
	@Override
	public void save(Alert alert) {
		alertRepository.save(alert);
	}
	
	@Override
	public void saveAndSend(Alert alert) {
		save(alert);
		List<String> emails = AlertUtil.parseReceivers("email", alert.getReceiver());
		if(null != emails && emails.size() > 0) {
			emailService.push(emails.toArray(new String[emails.size()]), alert.getInfoName(), alert.getMessage());
		}
		
	}
	
	@Override
	public boolean delById(Integer id) {
		try {
			alertRepository.delete(id);
		}catch(Exception e) {
			
			return false;
		}
		return true;
	}
	
	@Override
	public int delByInfo(int infoId) {
		return alertRepository.delete(infoId);
	}
	
	@Override
	public void save(List<Alert> alerts) {
		alertRepository.save(alerts);
	}
	
	@Override
	public Alert getById(Integer id) {
		return alertRepository.findOne(id);
	}
	
	@Override
	public List<Alert> listAll(){
		return (List<Alert>) alertRepository.findAll();
	}
	
	@Override
	public Page<Alert> listByPage(PlatformType group,int pageNum, int pageSize){
		Specification<Alert> spec = SpecificationUtil.<Alert>and().ep("group", group).orderByDESC("createdTime").build();
		return alertRepository.findAll(spec, new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public Page<Alert> listByPage(int infoId,PlatformType group,int pageNum, int pageSize){
		Specification<Alert> spec = SpecificationUtil.<Alert>and().ep("group", group).ep("infoId", infoId).orderByDESC("createdTime").build();
		return alertRepository.findAll(spec, new PageRequest(pageNum - 1, pageSize));
	}
}
