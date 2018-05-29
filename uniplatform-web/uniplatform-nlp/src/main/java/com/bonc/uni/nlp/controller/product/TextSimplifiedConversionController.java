package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.ITextSimplifiedConversionService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午12:03:42 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/text/simplified")
public class TextSimplifiedConversionController {
	
	@Autowired
	ITextSimplifiedConversionService textSimplifiedService;
	
	@RequestMapping(value = "/HongKong", method = { RequestMethod.GET, RequestMethod.POST })
	public String convert2HongKongAndMacaoTraditional(String text) {
		LogManager.Process("Process in controller: /nlap/admin/text/simplified/HongKong");

		String simplifiedInfo = textSimplifiedService.convert2HongKongAndMacaoTraditional(text);

		LogManager.Process("Process out controller: /nlap/admin/text/simplified/HongKong");
		return simplifiedInfo;
	}
	
	@RequestMapping(value = "/TaiWai", method = { RequestMethod.GET, RequestMethod.POST })
	public String convert2TaiWaiTraditional(String text) {
		LogManager.Process("Process in controller: /nlap/admin/text/simplified/TaiWai");

		String simplifiedInfo = textSimplifiedService.convert2TaiWaiTraditional(text);

		LogManager.Process("Process out controller: /nlap/admin/text/simplified/TaiWai");
		return simplifiedInfo;
	}
	
	@RequestMapping(value = "/simplifiedChinese", method = { RequestMethod.GET, RequestMethod.POST })
	public String simplifiedChinese(String text) {
		LogManager.Process("Process in controller: /nlap/admin/text/simplified/simplifiedChinese");

		String simplifiedInfo = textSimplifiedService.simplifiedChinese(text);

		LogManager.Process("Process out controller: /nlap/admin/text/simplified/simplifiedChinese");
		return simplifiedInfo;
	}

}
 