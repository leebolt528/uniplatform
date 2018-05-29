package com.bonc.uni.nlp.utils.model.trainer;

import com.bonc.uni.nlp.entity.strategy.Model;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.utils.model.IModelTrainer;

public class Word2VecGoogleTrainer implements IModelTrainer{

	@Override
	public String trainModel(Model model, String dataSetId) {
		throw new AdminException("该算法模型无训练方法");
	}

	@Override
	public void removeModel(String modelName) {
	}

}
