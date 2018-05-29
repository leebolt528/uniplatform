package com.bonc.uni.nlp.utils.model;

import com.bonc.uni.nlp.entity.strategy.Model;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月23日 上午11:53:41 
*/
public interface IModelTrainer {

	String trainModel(Model model, String dataSetId);

	void removeModel(String modelName);

}
 