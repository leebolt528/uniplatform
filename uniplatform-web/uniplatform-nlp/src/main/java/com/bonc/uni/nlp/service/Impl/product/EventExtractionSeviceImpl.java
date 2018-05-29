package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextEventExtractionClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.IEventExtractionService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午4:25:15 
*/
@Service
public class EventExtractionSeviceImpl implements IEventExtractionService{

	private TextEventExtractionClient client = TextEventExtractionClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	@Override
	public String extraction(String text) {
		LogManager.Method("Enter the Method : EventExtractionSeviceImpl/extraction");
		
		String textEventExtractionResult = client.eventExtraction(text);

		LogManager.Method("Out from the Method : EventExtractionSeviceImpl/extraction");
		return textEventExtractionResult;
	}

}
 