package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.ITextPMINewwordService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午6:02:38 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/pmi")
public class PMINewwordController {
	
	@Autowired
	ITextPMINewwordService textPMINewwordService;

	/**
	 * 新词发现
	 * @param filePath
	 * @return
	 */
	@RequestMapping(value = "/newword", method = { RequestMethod.GET, RequestMethod.POST })
	public String pmiProcess(String filePath) {
		LogManager.Process("Process in controller: /nlap/admin/pmi/newword");

		String pmiProcessInfo = textPMINewwordService.pmiProcess(filePath);

		LogManager.Process("Process out controller: /nlap/admin/pinyin/tagging");
		return pmiProcessInfo;
		
	}
}
 