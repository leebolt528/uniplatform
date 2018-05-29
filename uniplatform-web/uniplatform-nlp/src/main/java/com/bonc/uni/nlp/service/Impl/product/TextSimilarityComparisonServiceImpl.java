package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextShortSimilarityClient;
import com.bonc.text.sdk.client.TextSimilarityDetectionClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.ITextSimilarityComparisonService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月23日 下午1:40:13
 */
@Service
public class TextSimilarityComparisonServiceImpl implements ITextSimilarityComparisonService {

	private TextSimilarityDetectionClient client = TextSimilarityDetectionClient
			.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	private TextShortSimilarityClient clientShort = TextShortSimilarityClient
			.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	@Override
	public String textSimilarityCompare(String text1, String text2) {
		LogManager.Method("Enter the Method : TextSimilarityComparisonServiceImpl/textSimilarityCompare");

		String textSimilarityResult = client.textSimilarityComparison(text1, text2);

		LogManager.Method("Out from the Method : TextSimilarityComparisonServiceImpl/textSimilarityCompare");
		return textSimilarityResult;
	}

	@Override
	public String shortTextSimilarityComparison(String text1, String text2) {
		LogManager.Method("Enter the Method : TextSimilarityComparisonServiceImpl/shortTextSimilarityComparison");

		String textSimilarityResult = clientShort.shortTextSimilarityComparison(text1, text2);

		LogManager.Method("Out from the Method : TextSimilarityComparisonServiceImpl/shortTextSimilarityComparison");
		return textSimilarityResult;
	}

}
