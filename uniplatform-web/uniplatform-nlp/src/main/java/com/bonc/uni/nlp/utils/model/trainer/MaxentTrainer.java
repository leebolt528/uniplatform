package com.bonc.uni.nlp.utils.model.trainer;

import com.bonc.text.sdk.client.TextClassifyMaxEntClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.entity.strategy.Model;
import com.bonc.uni.nlp.utils.model.IModelTrainer;

public class MaxentTrainer implements IModelTrainer{

	@Override
	public String trainModel(Model model, String dataSetId) {
		String modelName = model.getName();
		return this.getClient().trainModelByDataSetId(modelName, dataSetId);
	}

	@Override
	public void removeModel(String modelName) {
		this.getClient().removeModel(modelName);
	}

	private TextClassifyMaxEntClient getClient(){
		TextClassifyMaxEntClient client = TextClassifyMaxEntClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);
		return client;
	}
}
