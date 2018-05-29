package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextCorrelationClient;
import com.bonc.text.sdk.entity.CorrelationTextType;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.strategy.IThemeRelatedService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月28日 下午2:04:52 
*/
@Service
public class ThemeRelatedServiceImpl implements IThemeRelatedService{

	private TextCorrelationClient client = TextCorrelationClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	/**
	 * criterion 标准语料 text 判断语料 flay 标准语料的输入形式是词(word)还是句子(text)，词的话注意要用空格或者逗号相隔
	 * 相关度
	 */
	@Override
	public String themeRelated(String baseText, String text, String type) {
		LogManager.Method("Enter the Method : themeRelated/themeRelated");

		CorrelationTextType flag = CorrelationTextType.text;
		switch (type) {
		case "word":
			flag = CorrelationTextType.word;
			break;
		case "text":
			flag = CorrelationTextType.text;
			break;
		default:
			break;
		}
		String result = client.getCorrelationSum(baseText, text, flag);
	
		LogManager.Method("Out from the Method : themeRelated/themeRelated");
		return result;
	}

}
 