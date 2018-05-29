package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.ITextRelationExtractService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午7:29:57 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/text/relation")
public class TextRelationExtractController {

	@Autowired
	ITextRelationExtractService extractService;
	
	/**
	 * <p>Title: getPersonToPersonRelation</p>
	 * <p>Description: 人-人关系</p>
	 * @param text
	 * @return
	 */
	@RequestMapping(value = "/p2p", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getPersonToPersonRelation(String text){
		LogManager.Process("Process in controller: /nlap/admin/text/relation/getPersonToPersonRelation");
		
		String result = extractService.getPersonToPersonRelation(text);
		
		LogManager.Process("Process out controller: /nlap/admin/text/relation/getPersonToPersonRelation");
		return result;
	}
	/**
	 * <p>Title: getPersonToPersonRelation</p>
	 * <p>Description: 人-机构 关系</p>
	 * @param text
	 * @return
	 */
	@RequestMapping(value = "/p2o", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getOrgToPersonSingleSentencePredict(String text){
		LogManager.Process("Process in controller: /nlap/admin/text/relation/getOrgToPersonSingleSentencePredict");
		
		String result = extractService.getOrgToPersonSingleSentencePredict(text);
		
		LogManager.Process("Process out controller: /nlap/admin/text/relation/getOrgToPersonSingleSentencePredict");
		return result;
	}
	
	/**
	 * <p>Title: getPersonToPersonRelation</p>
	 * <p>Description: 机构-机构 关系</p>
	 * @param text
	 * @return
	 */
	@RequestMapping(value = "/o2o", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getOrgToOrgSingleSentencePredict(String text){
		LogManager.Process("Process in controller: /nlap/admin/text/relation/getOrgToOrgSingleSentencePredict");
		
		String result = extractService.getOrgToOrgSingleSentencePredict(text);
		
		LogManager.Process("Process out controller: /nlap/admin/text/relation/getOrgToOrgSingleSentencePredict");
		return result;
	}
}
 