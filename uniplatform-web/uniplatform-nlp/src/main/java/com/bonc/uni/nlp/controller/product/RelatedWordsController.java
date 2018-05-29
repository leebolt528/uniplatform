package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.IRelatedWordsService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月25日 下午5:01:03 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/relatedwords")
public class RelatedWordsController {

	@Autowired
	IRelatedWordsService relatedWordsService;
	
	/**
	 * 相关词
	 * @param modelName
	 * @param word
	 * @return
	 */
	@RequestMapping(value = "/related", method = { RequestMethod.GET, RequestMethod.POST })
	public String getRelatedwords(String modelName, String word){
		LogManager.Process("Process in controller: /nlap/admin/words/related");

		String relatedwordsResult = relatedWordsService.getRelatedwords(modelName, word);

		LogManager.Process("Process out controller: /nlap/admin/words/related");
		return relatedwordsResult;
	}
}
 