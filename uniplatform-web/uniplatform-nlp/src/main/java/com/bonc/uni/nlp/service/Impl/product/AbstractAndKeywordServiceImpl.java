package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextAbsKeywordClient;
import com.bonc.text.sdk.client.en.TextKeywordAndSummaryEnglishClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.IAbstractAndKeywordService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月21日 上午10:15:46
 */
@Service
public class AbstractAndKeywordServiceImpl implements IAbstractAndKeywordService {

	private TextAbsKeywordClient client = TextAbsKeywordClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	private TextKeywordAndSummaryEnglishClient clientEn = TextKeywordAndSummaryEnglishClient
			.getInstance(SystemConfig.PROCESSOR_DOMAIN_EN);

	@Override
	public String getAbstract(String title, String content) {
		LogManager.Method("Enter the Method : AbstractServiceImpl/getAbstract");
		String abstractResult = client.getSummaryAndKeywords(title, content, SystemConfig.PROCESSOR_KEYWORD_NUM,
				SystemConfig.PROCESSOR_SUMMARY_PERCENT, SystemConfig.PROCESSOR_SUMMARY_SENTENCE_NUM);

		LogManager.Method("Out from the Method : AbstractServiceImpl/getAbstract");
		return abstractResult;
	}

	@Override
	public String getAbstractEn(String title, String content) {
		LogManager.Method("Enter the Method : AbstractServiceImpl/getAbstractEn");
		String abstractResult = clientEn.getKeywordsAndSummaryEnglishJ(title, content,
				SystemConfig.PROCESSOR_KEYWORD_NUM, SystemConfig.PROCESSOR_SUMMARY_PERCENT,
				SystemConfig.PROCESSOR_SUMMARY_SENTENCE_NUM);

		LogManager.Method("Out from the Method : AbstractServiceImpl/getAbstractEn");
		return abstractResult;
	}

}
