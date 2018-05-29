package com.bonc.uni.nlp.utils.model.trainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bonc.text.sdk.client.TextClassifyAdaboostClient;
import com.bonc.text.sdk.entity.Classifier;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.constant.SystemConstant;
import com.bonc.uni.nlp.entity.strategy.Model;
import com.bonc.uni.nlp.entity.strategy.StrategyConstant;
import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.uni.nlp.utils.Resources;
import com.bonc.uni.nlp.utils.model.IModelTrainer;
import com.bonc.usdp.odk.common.exception.PathNotFoundException;
import com.bonc.usdp.odk.logmanager.LogManager;

public class AdaboostTrainer implements IModelTrainer {
	@Override
	public String trainModel(Model model, String dataSetId) {
		String modelName = model.getName();
		
		Resources resource = null;
		try {
			resource = new Resources(PathUtil.getConfigPath() + File.separator + SystemConstant.APPLICATION);
		} catch (PathNotFoundException e) {
			LogManager.Exception(e);
		}
		List<Classifier> classifiers = new ArrayList<>();
		Classifier bayesClassifier = new Classifier();
		bayesClassifier.setClassPath(StrategyConstant.ALGORITHM_CLASSIFY_BAYES);
		bayesClassifier.setModelName(resource.getProperty("bonc.usdp.text.classify.adaboost.bayes.model"));
		classifiers.add(bayesClassifier);
		
		Classifier decisionTreeClassifier = new Classifier();
		decisionTreeClassifier.setClassPath(StrategyConstant.ALGORITHM_CLASSIFY_DECISIONTREE);
		decisionTreeClassifier.setModelName(resource.getProperty("bonc.usdp.text.classify.adaboost.decisiontree.model"));
		classifiers.add(decisionTreeClassifier);
		
		int iterationCount = resource.getIntProperty("bonc.usdp.text.classify.adaboost.iterationCount", 10);
		return this.getClient().trainModelByDataSetId(dataSetId, modelName, classifiers, iterationCount);
	}

	@Override
	public void removeModel(String modelName) {
		this.getClient().removeModel(modelName);
	}
	
	private TextClassifyAdaboostClient getClient() {
		TextClassifyAdaboostClient client = TextClassifyAdaboostClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);
		return client;
	}
}
