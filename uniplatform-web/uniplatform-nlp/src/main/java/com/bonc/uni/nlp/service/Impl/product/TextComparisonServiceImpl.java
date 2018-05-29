package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextContrastClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.ITextComparisonService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月22日 下午8:09:17 
*/
@Service
public class TextComparisonServiceImpl implements ITextComparisonService{
	
	private TextContrastClient client = TextContrastClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	@Override
	public String textComparison(String text1, String text2) {
		LogManager.Method("Enter the Method : TextComparisonServiceImpl/extractionEntity");
		
		String textComparisonResult = client.getDiffCleanUp(text1, text2);

		LogManager.Method("Out from the Method : TextComparisonServiceImpl/extractionEntity");
		return textComparisonResult;
	}

}
 