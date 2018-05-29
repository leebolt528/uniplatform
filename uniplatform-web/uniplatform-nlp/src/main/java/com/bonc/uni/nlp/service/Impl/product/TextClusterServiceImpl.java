package com.bonc.uni.nlp.service.Impl.product;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextClusterClient;
import com.bonc.text.sdk.client.en.TextClusterEnglishClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.ITextClusterService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月25日 下午5:27:06
 */
@Service
public class TextClusterServiceImpl implements ITextClusterService {

	private TextClusterClient client = TextClusterClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	private TextClusterEnglishClient clientEn = TextClusterEnglishClient.getInstance(SystemConfig.PROCESSOR_DOMAIN_EN);

	@Override
	public String cluster(String filePath) {
		LogManager.Method("Enter the Method : TextClusterServiceImpl/cluster");

		String result = client.clusters(filePath);

		LogManager.Method("Out from the Method : TextClusterServiceImpl/cluster");

		return result;
	}

	@Override
	public String clusterEn(String textPath) {
		LogManager.Method("Enter the Method : TextClusterServiceImpl/clusterEn");

		String result = clientEn.getClusterEnglishMultistageJ(textPath);

		LogManager.Method("Out from the Method : TextClusterServiceImpl/clusterEn");

		return result;
	}

}
