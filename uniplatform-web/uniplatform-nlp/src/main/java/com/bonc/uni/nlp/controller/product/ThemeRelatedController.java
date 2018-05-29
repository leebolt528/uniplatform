package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.strategy.IThemeRelatedService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月28日 下午2:04:21 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/theme")
public class ThemeRelatedController {

	@Autowired
	IThemeRelatedService themeRelatedService;
	
	/**
	 * 主题相关
	 * @param criterion 标准语料
	 * @param text 判断语料
	 * @param flay 标准语料的输入形式是词(word)还是句子(text)，词的话注意要用空格或者逗号相隔
	 * @return 相关度
	 */
	@RequestMapping(value = "/relation", method = { RequestMethod.GET, RequestMethod.POST })
	public String themeRelated(String criterion, String text,String flay) {
		LogManager.Process("Process in controller: /nlap/admin/theme/relation");

		String taggingInfo = themeRelatedService.themeRelated(criterion, text, flay);

		LogManager.Process("Process out controller: /nlap/admin/theme/relation");
		return taggingInfo;
	}
}
 