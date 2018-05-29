package com.bonc.uni.nlp.service.Impl.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextSentimentAnalysisClient;
import com.bonc.text.sdk.entity.EmotionCeil;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.entity.product.EmotionResult;
import com.bonc.uni.nlp.service.product.ISentimentAnalysisService;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午5:48:17 
*/
@Service
public class SentimentAnalysisServiceImpl implements ISentimentAnalysisService{
	
	private TextSentimentAnalysisClient client = TextSentimentAnalysisClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	@Override
	public List<EmotionResult> getEmotionCeils(String text) {
		LogManager.Method("Enter the Method : SentimentAnalysisServiceImpl/sentimentAnalysis");
		
		List<EmotionResult> results = new ArrayList<>();
		//对原文进行分句
	
		String[] textStr = text.split("？|。|！");
		
		for(String sentence : textStr){
			if(StringUtil.isEmpty(sentence.trim())){
				continue;
			}
			EmotionResult result = new EmotionResult();
			result.setSentence(sentence);
			List<String> evals = new ArrayList<>();
			
			List<EmotionCeil> ceils = client.getExtractEmotionCeil(sentence);
			
			if(ceils==null){
				continue;
			}else{
				for (EmotionCeil ceil : ceils) {
					evals.add(ceil.getEvalStr() + ceil.getEmotionStr());
				}
				result.setEvals(evals);
				
				results.add(result);				
			}
		}
		
		LogManager.Method("Out from the Method : SentimentAnalysisServiceImpl/sentimentAnalysis");
		return results;
	}

	@Override
	public String analysisEmotion(String text) {
		LogManager.Method("Enter the Method : PinYinTaggingServiceImpl/textPinyinTagging");
		
		String analysisResult = client.getTextScore(text);

		LogManager.Method("Out from the Method : PinYinTaggingServiceImpl/textPinyinTagging");
		return analysisResult;
	}

}
 