package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextRelationExtractClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.ITextRelationExtractService;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午7:30:30 
*/
@Service
public class TextRelationExtractServiceImpl implements ITextRelationExtractService{
	
	private TextRelationExtractClient client = TextRelationExtractClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	@Override
	public String getPersonToPersonRelation(String text) {
		return client.getPersonToPersonRelation(text);
	}
	
	@Override
	public String getOrgToPersonSingleSentencePredict(String text) {
		return client.getOrgToPersonSingleSentencePredict(text);
	}
	
	@Override
	public String getOrgToOrgSingleSentencePredict(String text) {
		return client.getOrgToOrgSingleSentencePredict(text);
	}

}
 