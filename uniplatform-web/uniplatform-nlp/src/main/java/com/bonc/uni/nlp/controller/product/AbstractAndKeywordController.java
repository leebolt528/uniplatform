package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.IAbstractAndKeywordService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月21日 上午10:13:23
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/abskeyword")
public class AbstractAndKeywordController {

	@Autowired
	IAbstractAndKeywordService abstractService;

	@RequestMapping(value = "/abstext", method = { RequestMethod.GET, RequestMethod.POST })
	public String getAbstractKeywords(String title, String content) {
		LogManager.Process("Process in controller: /nlap/admin/abskeyword/abstext");

		String abstractResult = abstractService.getAbstract(title, content);

		LogManager.Process("Process out controller: /nlap/admin/abskeyword/abstext");
		return abstractResult;
	}
	
	@RequestMapping(value = "/abstext/en", method = { RequestMethod.GET, RequestMethod.POST })
	public String getAbstractKeywordsEn(String title, String content) {
		LogManager.Process("Process in controller: /nlap/admin/abskeyword/abstext/en");

		String abstractResult = abstractService.getAbstractEn(title, content);

		LogManager.Process("Process out controller: /nlap/admin/abskeyword/abstext/en");
		return abstractResult;
	}

}
