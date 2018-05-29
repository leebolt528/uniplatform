package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.ITextSimilarityComparisonService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午1:39:03 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/text/similarity")
public class TextSimilarityComparisonController {
	
	@Autowired
	ITextSimilarityComparisonService textSimilarityCompareService;

	@RequestMapping(value = "/compare", method = { RequestMethod.GET, RequestMethod.POST })
	public String textSimilarityCompare(String text1, String text2) {
		LogManager.Process("Process in controller: /nlap/admin/text/similarity/compare");

		String similarityInfo = textSimilarityCompareService.textSimilarityCompare(text1, text2);

		LogManager.Process("Process out controller: /nlap/admin/text/similarity/compare");
		return similarityInfo;
	}
	
	@RequestMapping(value = "/compare/short", method = { RequestMethod.GET, RequestMethod.POST })
	public String shortTextSimilarityComparison(String text1, String text2) {
		LogManager.Process("Process in controller: /nlap/admin/text/similarity/compare/short");

		String similarityInfo = textSimilarityCompareService.shortTextSimilarityComparison(text1, text2);

		LogManager.Process("Process out controller: /nlap/admin/text/similarity/compare/short");
		return similarityInfo;
	}
}
 