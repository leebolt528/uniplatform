package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextPMINewwordClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.ITextPMINewwordService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午6:03:34 
*/
@Service
public class TextPMINewwordServiceImpl implements ITextPMINewwordService{

	private TextPMINewwordClient client = TextPMINewwordClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);
	
	@Override
	public String pmiProcess(String filePath) {
		LogManager.Method("Enter the Method : TextPMINewwordServiceImpl/pmiProcess");
		
		String pmiProcessResult = client.identifyNewWords(filePath);

		LogManager.Method("Out from the Method : TextPMINewwordServiceImpl/pmiProcess");
		return pmiProcessResult;
	}

}
 