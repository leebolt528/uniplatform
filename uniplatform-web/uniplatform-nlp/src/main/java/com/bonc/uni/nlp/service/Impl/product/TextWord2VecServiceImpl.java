package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextWord2VecClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.ITextWord2VecService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午5:03:01 
*/
@Service
public class TextWord2VecServiceImpl implements ITextWord2VecService{

	private TextWord2VecClient client = TextWord2VecClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	@Override
	public String word2Vec(String word) {
		LogManager.Method("Enter the Method : TextWord2VecServiceImpl/word2Vec");
		String word2VecResult = client.word2vecMulti(word);
		
		LogManager.Method("Out from the Method : TextWord2VecServiceImpl/word2Vec");
		return word2VecResult;
	}
	
}
 