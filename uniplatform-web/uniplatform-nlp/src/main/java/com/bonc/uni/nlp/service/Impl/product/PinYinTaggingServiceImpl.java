package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextPinYinTaggingClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.IPinYinTaggingService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 上午11:37:03 
*/
@Service
public class PinYinTaggingServiceImpl implements IPinYinTaggingService{

	private TextPinYinTaggingClient client = TextPinYinTaggingClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	@Override
	public String textPinyinTagging(String text) {
		LogManager.Method("Enter the Method : PinYinTaggingServiceImpl/textPinyinTagging");
		
		String textPinyinTaggingResult = client.convert2PinYin(text);

		LogManager.Method("Out from the Method : PinYinTaggingServiceImpl/textPinyinTagging");
		return textPinyinTaggingResult;
	}

}
 