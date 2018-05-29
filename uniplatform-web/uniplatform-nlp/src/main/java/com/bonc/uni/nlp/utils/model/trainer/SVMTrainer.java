package com.bonc.uni.nlp.utils.model.trainer;

import com.bonc.text.sdk.client.TextClassifySvmClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.entity.strategy.Model;
import com.bonc.uni.nlp.utils.model.IModelTrainer;

public class SVMTrainer implements IModelTrainer{

	@Override
	public String trainModel(Model model, String dataSetId) {
		String modelName = model.getName();
		String modelInfo = getClient().trainModelByDataSetId(modelName, dataSetId);
		return modelInfo;
	}

	@Override
	public void removeModel(String modelName) {
		this.getClient().deleteModel(modelName);
	}
	
	private TextClassifySvmClient getClient(){
		TextClassifySvmClient client = TextClassifySvmClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);
		return client;
	}

}
