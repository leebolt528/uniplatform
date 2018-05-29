package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.IAutoWordSegmentService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月21日 上午11:47:09 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/segment")
public class AutoWordSegmentController {

	@Autowired
	IAutoWordSegmentService autoWordSegmentService;
	
	@RequestMapping(value = "/auto/segment", method = { RequestMethod.GET, RequestMethod.POST })
	public String getSegment(String text){
		LogManager.Process("Process in controller: /nlap/admin/segment/auto/segment");
		String segmentInfo = autoWordSegmentService.getSegment(text);
		
		LogManager.Process("Process out controller: /nlap/admin/segment/auto/segment");
		return segmentInfo;
	}
	
	@RequestMapping(value = "/auto/segment/en", method = { RequestMethod.GET, RequestMethod.POST })
	public String getSegmentEnglish(String text){
		LogManager.Process("Process in controller: /nlap/admin/segment/auto/segment/en");
		String segmentInfo = autoWordSegmentService.getSegmentEnglish(text);
		
		LogManager.Process("Process out controller: /nlap/admin/segment/auto/segment/en");
		return segmentInfo;
	}
}
 