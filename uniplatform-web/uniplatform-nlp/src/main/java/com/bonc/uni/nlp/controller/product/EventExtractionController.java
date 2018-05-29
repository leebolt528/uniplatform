package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.IEventExtractionService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午4:24:38 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/event")
public class EventExtractionController {
	
	@Autowired
	IEventExtractionService eventExtractionService;
	
	@RequestMapping(value = "/extraction", method = { RequestMethod.GET, RequestMethod.POST })
	public String extraction(String text) {
		LogManager.Process("Process in controller: /nlap/admin/event/extraction");

		String taggingInfo = eventExtractionService.extraction(text);

		LogManager.Process("Process out controller: /nlap/admin/event/extraction");
		return taggingInfo;
	}

}
 