package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.ITextWord2VecService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午5:02:27 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/word2Vec")
public class TextWord2VecController {
	
	@Autowired
	ITextWord2VecService textWord2VecService;
	
	/**
	 * 词相关
	 * @param word
	 * @return
	 */
	@RequestMapping(value = "/word2Vectext", method = { RequestMethod.GET, RequestMethod.POST })
	public String word2Vec(String word) {
		LogManager.Process("Process in controller: /nlap/admin/word2Vec/word2Vectext");

		String word2VecResult = textWord2VecService.word2Vec(word);

		LogManager.Process("Process out controller: /nlap/admin/word2Vec/word2Vectext");
		return word2VecResult;
	}

}
 