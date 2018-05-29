package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextEntityRecognitionClient;
import com.bonc.text.sdk.client.en.TextEntityRecognitionEnglishClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.IEntityExtractionService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月21日 下午2:34:20
 */
@Service
public class EntityExtractionServiceImpl implements IEntityExtractionService {

	private TextEntityRecognitionClient client = TextEntityRecognitionClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	private TextEntityRecognitionEnglishClient clientEn = TextEntityRecognitionEnglishClient
			.getInstance(SystemConfig.PROCESSOR_DOMAIN_EN);

	@Override
	public String extractionEntity(String text) {
		LogManager.Method("Enter the Method : EntityExtractionServiceImpl/extractionEntity");

		String extractionResult = client.extractAllEntity(text);

		LogManager.Method("Out from the Method : EntityExtractionServiceImpl/extractionEntity");
		return extractionResult;
	}

	@Override
	public String extractionEntityEn(String text) {
		LogManager.Method("Enter the Method : EntityExtractionServiceImpl/extractionEntityEn");

		String extractionResult = clientEn.getEntityJ(text);

		LogManager.Method("Out from the Method : EntityExtractionServiceImpl/extractionEntityEn");
		return extractionResult;
	}

}
