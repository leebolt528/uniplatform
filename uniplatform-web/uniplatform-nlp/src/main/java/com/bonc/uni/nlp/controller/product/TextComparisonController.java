package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.ITextComparisonService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月22日 下午8:05:39
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/text/comparison")
public class TextComparisonController {

	@Autowired
	ITextComparisonService textComparisonService;

	/**
	 * 文本对比
	 * @param text1
	 * @param text2
	 * @return
	 */
	@RequestMapping(value = "/compare", method = { RequestMethod.GET, RequestMethod.POST })
	public String textComparison(String text1, String text2) {
		LogManager.Process("Process in controller: /nlap/admin/text/comparison/compare");

		String comparisonInfo = textComparisonService.textComparison(text1, text2);

		LogManager.Process("Process out controller: /nlap/admin/text/comparison/compare");
		return comparisonInfo;
	}

}
