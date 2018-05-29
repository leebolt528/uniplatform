package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.IPinYinTaggingService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 上午11:35:57 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/pinyin")
public class PinYinTaggingController {

	@Autowired
	IPinYinTaggingService pinYinTaggingService;
	
	@RequestMapping(value = "/tagging", method = { RequestMethod.GET, RequestMethod.POST })
	public String textPinyinTagging(String text) {
		LogManager.Process("Process in controller: /nlap/admin/pinyin/tagging");

		String taggingInfo = pinYinTaggingService.textPinyinTagging(text);

		LogManager.Process("Process out controller: /nlap/admin/pinyin/tagging");
		return taggingInfo;
	}

}
 