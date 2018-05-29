package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextRelatedWordClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.IRelatedWordsService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月25日 下午5:02:36 
*/
@Service
public class RelatedWordsServiceImpl implements IRelatedWordsService{

	private TextRelatedWordClient client = TextRelatedWordClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	@Override
	public String getRelatedwords(String modelName, String word) {
		LogManager.Method("Enter the Method : RelatedWordsServiceImpl/getRelatedwords");
		
		String getRelatedwordsResult = client.getSecondaryRelatedWord(modelName, word);

		LogManager.Method("Out from the Method : RelatedWordsServiceImpl/getRelatedwords");
		return getRelatedwordsResult;
	}

}
 