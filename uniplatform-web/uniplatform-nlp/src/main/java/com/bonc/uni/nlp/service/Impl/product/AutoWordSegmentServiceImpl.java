package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextParticipleClient;
import com.bonc.text.sdk.client.en.TextSegmentEnglishClient;
import com.bonc.text.sdk.entity.SegmentType;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.IAutoWordSegmentService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月21日 上午11:48:28
 */
@Service
public class AutoWordSegmentServiceImpl implements IAutoWordSegmentService {

	private TextParticipleClient client = TextParticipleClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);
	
	private TextSegmentEnglishClient clientEn = TextSegmentEnglishClient.getInstance(SystemConfig.PROCESSOR_DOMAIN_EN);

	@Override
	public String getSegment(String text) {
		LogManager.Method("Enter the Method : AutoWordSegmentServiceImpl/getSegment");
		String segmentResult = client.defaultSegment(SegmentType.Nlp, text);

		LogManager.Method("Out from the Method : AutoWordSegmentServiceImpl/getSegment");
		return segmentResult;
	}

	@Override
	public String getSegmentEnglish(String text) {
		LogManager.Method("Enter the Method : AutoWordSegmentServiceImpl/getSegmentEnglish");
		String segmentResult = clientEn.getSegmentJ(text);

		LogManager.Method("Out from the Method : AutoWordSegmentServiceImpl/getSegmentEnglish");
		return segmentResult;
	}

}
