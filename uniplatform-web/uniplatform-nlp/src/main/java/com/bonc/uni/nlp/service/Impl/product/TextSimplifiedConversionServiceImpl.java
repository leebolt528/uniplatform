package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextSimplifiedTraditionalClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.ITextSimplifiedConversionService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午12:39:00 
*/
@Service
public class TextSimplifiedConversionServiceImpl implements ITextSimplifiedConversionService{
	
	TextSimplifiedTraditionalClient client = TextSimplifiedTraditionalClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	@Override
	public String convert2HongKongAndMacaoTraditional(String content) {
		LogManager.Method("Enter the Method : TextSimplifiedServiceImpl/convert2HongKongAndMacaoTraditionalJ");
		String textComparisonResult = client.convert2HongKongAndMacaoTraditional(content);

		LogManager.Method("Out from the Method : TextSimplifiedServiceImpl/convert2HongKongAndMacaoTraditionalJ");
		return textComparisonResult;
	}

	@Override
	public String convert2TaiWaiTraditional(String content) {
		LogManager.Method("Enter the Method : TextSimplifiedServiceImpl/convert2TaiWaiTraditional");
		String textComparisonResult = client.convert2TaiWaiTraditional(content);

		LogManager.Method("Out from the Method : TextSimplifiedServiceImpl/convert2TaiWaiTraditional");
		return textComparisonResult;
	}

	@Override
	public String simplifiedChinese(String content) {
		LogManager.Method("Enter the Method : TextSimplifiedServiceImpl/simplifiedChinese");
		String textComparisonResult = client.convert2SimplifiedChinese(content);

		LogManager.Method("Out from the Method : TextSimplifiedServiceImpl/simplifiedChinese");
		return textComparisonResult;
	}

}
 