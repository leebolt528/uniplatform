package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.IEntityExtractionService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月21日 下午2:33:14 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/entity")
public class EntityExtractionController {
	
	@Autowired
	IEntityExtractionService entityExtractionService;
	
	@RequestMapping(value = "/extraction", method = { RequestMethod.GET, RequestMethod.POST })
	public String extractionEntity(String text){
		LogManager.Process("Process in controller: /nlap/admin/entity/extraction");
		
		String extractionInfo = entityExtractionService.extractionEntity(text);
		
		LogManager.Process("Process out controller: /nlap/admin/entity/extraction");
		return extractionInfo;
		
	}
	
	@RequestMapping(value = "/extraction/en", method = { RequestMethod.GET, RequestMethod.POST })
	public String extractionEntityEn(String text){
		LogManager.Process("Process in controller: /nlap/admin/entity/extraction/en");
		
		String extractionInfo = entityExtractionService.extractionEntityEn(text);
		
		LogManager.Process("Process out controller: /nlap/admin/entity/extraction/en");
		return extractionInfo;
		
	}

}
 