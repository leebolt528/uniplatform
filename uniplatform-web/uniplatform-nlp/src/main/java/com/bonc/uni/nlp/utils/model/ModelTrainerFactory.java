package com.bonc.uni.nlp.utils.model;

import java.util.HashMap;
import java.util.Map;

import com.bonc.uni.nlp.entity.strategy.Model;
import com.bonc.uni.nlp.entity.strategy.StrategyConstant;
import com.bonc.uni.nlp.exception.BoncException;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月23日 上午11:51:56 
*/
public class ModelTrainerFactory {
	
	private static ModelTrainerFactory instance;
	/**
	 * 存放算法和对应的model训练类名
	 */
	private Map<String, String> algModels;
	
	private ModelTrainerFactory(){
		algModels = new HashMap<>();
		algModels.put(StrategyConstant.ALGORITHM_CLASSIFY_ADABOOST, "com.bonc.uni.nlp.utils.model.trainer.AdaboostTrainer");
		algModels.put(StrategyConstant.ALGORITHM_CLASSIFY_BAYES, "com.bonc.uni.nlp.utils.model.trainer.BayesTrainer");
		algModels.put(StrategyConstant.ALGORITHM_CLASSIFY_DECISIONTREE, "com.bonc.uni.nlp.utils.model.trainer.DecisionTreeTrainer");
		algModels.put(StrategyConstant.ALGORITHM_CLASSIFY_MAXENT, "com.bonc.uni.nlp.utils.model.trainer.MaxentTrainer");
		algModels.put(StrategyConstant.ALGORITHM_CLASSIFY_SVM, "com.bonc.uni.nlp.utils.model.trainer.SVMTrainer");
		algModels.put(StrategyConstant.ALGORITHM_WORD2VEC_GOOGLE, "com.bonc.uni.nlp.utils.model.trainer.Word2VecGoogleTrainer");
		algModels.put(StrategyConstant.ALGORITHM_WORD2VEC_JAVA, "com.bonc.uni.nlp.utils.model.trainer.Word2VecJavaTrainer");
	}
	
	public synchronized static ModelTrainerFactory getInstance(){
		if(null == instance){
			instance = new ModelTrainerFactory();
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public IModelTrainer getModelTrainer(Model model, String algorithmName) throws BoncException{
		IModelTrainer trainer = null;
		String trainerClassName = algModels.get(algorithmName);
		if(null == trainerClassName){
			throw new BoncException("找不到训练类");
		}
		try {
			Class<IModelTrainer> trainerClass = (Class<IModelTrainer>) Class.forName(trainerClassName);
			trainer = trainerClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			LogManager.Exception(e);
		}
		return trainer;
	}
}
 