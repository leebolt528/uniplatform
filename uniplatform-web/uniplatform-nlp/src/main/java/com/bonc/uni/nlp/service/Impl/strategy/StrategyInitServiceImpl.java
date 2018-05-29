package com.bonc.uni.nlp.service.Impl.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.uni.nlp.dao.FuncitonRepository;
import com.bonc.uni.nlp.dao.strategy.AlgorithmRepository;
import com.bonc.uni.nlp.dao.strategy.FunctionDependFunctionRepository;
import com.bonc.uni.nlp.dao.strategy.ModelRepository;
import com.bonc.uni.nlp.dao.strategy.StrategyDependStrategyRepository;
import com.bonc.uni.nlp.dao.strategy.StrategyRepository;
import com.bonc.uni.nlp.entity.dic.Function;
import com.bonc.uni.nlp.entity.strategy.Algorithm;
import com.bonc.uni.nlp.entity.strategy.FunctionDependFunction;
import com.bonc.uni.nlp.entity.strategy.Strategy;
import com.bonc.uni.nlp.entity.strategy.StrategyConstant;
import com.bonc.uni.nlp.service.strategy.IStrategyMgmtService;
import com.bonc.uni.nlp.service.strategy.IStrategyService;

/**
 * @author : GaoQiuyuer
 * @version: 2017年11月1日 下午5:00:32
 */
@Service
public class StrategyInitServiceImpl implements IStrategyService {

	@Autowired
	IStrategyMgmtService strategyMgmtSevice;
	@Autowired
	IStrategyService strategyService;
	@Autowired
	FuncitonRepository funcitonRepository;
	@Autowired
	AlgorithmRepository algorithmRepository;
	@Autowired
	StrategyRepository strategyRepository;
	@Autowired
	ModelRepository modelRepository;
	@Autowired
	FunctionDependFunctionRepository functionDependFunctionRepository;
	@Autowired
	StrategyDependStrategyRepository strategyDependStrategyRepository;

	@Override
	public void initFunction() {
		this.initSegmentFunction();
		this.initAbstractFunction();
		this.initEntityExtractionFunction();
		this.initClassifyFunction();
		this.initSentimentFunction();
		this.initWord2VecFunction();
		this.initClusterFunction();
		this.initNewwordFunction();
		this.initEventFunction();
		this.initPinyinFunction();
		this.initCouplingFunction();
	}

	private void initSegmentFunction() {

		Function segmentFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_SEGMENT);

		if (null == segmentFunction) {
			segmentFunction = new Function();
			segmentFunction.setName(StrategyConstant.FUNCTION_SEGMENT);
			segmentFunction.setDisplayName("自动分词");
			segmentFunction.setIndex(1);
			funcitonRepository.save(segmentFunction);
		}
	}

	private void initAbstractFunction() {

		Function abstractFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_ABSTRACT);

		if (null == abstractFunction) {
			abstractFunction = new Function();
			abstractFunction.setName(StrategyConstant.FUNCTION_ABSTRACT);
			abstractFunction.setDisplayName("摘要关键词");
			abstractFunction.setIndex(2);
			funcitonRepository.save(abstractFunction);
		}
	}

	private void initEntityExtractionFunction() {

		Function entityFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_ENTITYEXTRACTION);

		if (null == entityFunction) {
			entityFunction = new Function();
			entityFunction.setName(StrategyConstant.FUNCTION_ENTITYEXTRACTION);
			entityFunction.setDisplayName("实体抽取");
			entityFunction.setIndex(3);
			funcitonRepository.save(entityFunction);
		}
	}

	private void initClassifyFunction() {

		Function classifyFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_CLASSIFY);

		if (null == classifyFunction) {
			classifyFunction = new Function();
			classifyFunction.setName(StrategyConstant.FUNCTION_CLASSIFY);
			classifyFunction.setDisplayName("文本分类");
			classifyFunction.setIndex(4);
			funcitonRepository.save(classifyFunction);
		}

	}

	private void initEventFunction() {

		Function eventFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_EVENTEXTRACTION);

		if (null == eventFunction) {
			eventFunction = new Function();
			eventFunction.setName(StrategyConstant.FUNCTION_EVENTEXTRACTION);
			eventFunction.setDisplayName("事件抽取");
			eventFunction.setIndex(5);
			funcitonRepository.save(eventFunction);
		}
	}

	private void initSentimentFunction() {

		Function sentimentFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_SENTIMENTANALYSIS);

		if (null == sentimentFunction) {
			sentimentFunction = new Function();
			sentimentFunction.setName(StrategyConstant.FUNCTION_SENTIMENTANALYSIS);
			sentimentFunction.setDisplayName("情感分析");
			sentimentFunction.setIndex(6);
			funcitonRepository.save(sentimentFunction);
		}
	}

	private void initNewwordFunction() {

		Function newwordFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_NEWWORD);

		if (null == newwordFunction) {
			newwordFunction = new Function();
			newwordFunction.setName(StrategyConstant.FUNCTION_NEWWORD);
			newwordFunction.setDisplayName("新词发现");
			newwordFunction.setIndex(7);
			funcitonRepository.save(newwordFunction);
		}
	}

	private void initWord2VecFunction() {

		Function w2vFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_WORD2VEC);

		if (null == w2vFunction) {
			w2vFunction = new Function();
			w2vFunction.setName(StrategyConstant.FUNCTION_WORD2VEC);
			w2vFunction.setDisplayName("word2vec");
			w2vFunction.setIndex(8);
			funcitonRepository.save(w2vFunction);
		}
	}

	private void initClusterFunction() {

		Function clusterFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_CLUSTER);

		if (null == clusterFunction) {
			clusterFunction = new Function();
			clusterFunction.setName(StrategyConstant.FUNCTION_CLUSTER);
			clusterFunction.setDisplayName("文本聚类");
			clusterFunction.setIndex(9);
			funcitonRepository.save(clusterFunction);
		}
	}

	private void initPinyinFunction() {

		Function pinyinFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_PINYIN);

		if (null == pinyinFunction) {
			pinyinFunction = new Function();
			pinyinFunction.setName(StrategyConstant.FUNCTION_PINYIN);
			pinyinFunction.setDisplayName("拼音标注");
			pinyinFunction.setIndex(10);
			funcitonRepository.save(pinyinFunction);
		}
	}

	private void initCouplingFunction() {

		Function couplingFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_COUPLING);

		if (null == couplingFunction) {
			couplingFunction = new Function();
			couplingFunction.setName(StrategyConstant.FUNCTION_COUPLING);
			couplingFunction.setDisplayName("简繁转换");
			couplingFunction.setIndex(11);
			funcitonRepository.save(couplingFunction);
		}
	}

	@Override
	public void initAlgorithm() {
		this.initSegmentAlgorithm();
		this.initAbstractAlgorithm();
		this.initEntityExtractionAlgorithmn();
		this.initClassifyAlgorithm();
		this.initSentimentAlgorithm();
		this.initWord2VecAlgorithm();
		this.initClusterAlgorithm();
		this.initNewwordAlgorithm();
		this.initEventAlgorithmn();
		this.initPinyinAlgorithm();
		this.initCouplingAlgorithm();
	}

	private void initSegmentAlgorithm() {
		Function segmentFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_SEGMENT);
		
		Algorithm algorithm00 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_SEGMENT_BASE);
		if (null == algorithm00) {
			algorithm00 = new Algorithm();
			algorithm00.setName(StrategyConstant.ALGORITHM_SEGMENT_BASE);
			algorithm00.setDisplayName("BASE分词");
			algorithm00.setFunctionId(segmentFunction.getId());
			algorithm00.setHasDic(1);
			algorithmRepository.save(algorithm00);
		}

		Algorithm algorithm01 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_SEGMENT_DIC);
		if (null == algorithm01) {
			algorithm01 = new Algorithm();
			algorithm01.setName(StrategyConstant.ALGORITHM_SEGMENT_DIC);
			algorithm01.setDisplayName("DIC分词");
			algorithm01.setFunctionId(segmentFunction.getId());
			algorithm01.setHasDic(1);
			algorithmRepository.save(algorithm01);
		}

		Algorithm algorithm02 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_SEGMENT_INDEX);
		if (null == algorithm02) {
			algorithm02 = new Algorithm();
			algorithm02.setName(StrategyConstant.ALGORITHM_SEGMENT_INDEX);
			algorithm02.setDisplayName("INDEX分词");
			algorithm02.setFunctionId(segmentFunction.getId());
			algorithm02.setHasDic(1);
			algorithmRepository.save(algorithm02);
		}

		Algorithm algorithm03 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_SEGMENT_NLP);
		if (null == algorithm03) {
			algorithm03 = new Algorithm();
			algorithm03.setName(StrategyConstant.ALGORITHM_SEGMENT_NLP);
			algorithm03.setDisplayName("NLP分词");
			algorithm03.setFunctionId(segmentFunction.getId());
			algorithm03.setHasDic(1);
			algorithmRepository.save(algorithm03);
		}

		Algorithm algorithm04 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_SEGMENT_TO);
		if (null == algorithm04) {
			algorithm04 = new Algorithm();
			algorithm04.setName(StrategyConstant.ALGORITHM_SEGMENT_TO);
			algorithm04.setDisplayName("TO分词");
			algorithm04.setFunctionId(segmentFunction.getId());
			algorithm04.setHasDic(1);
			algorithmRepository.save(algorithm04);
		}
	}

	/**
	 * 
	 */
	private void initAbstractAlgorithm() {
		Function abstractFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_ABSTRACT);

		Algorithm algorithm01 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_ABSTRACT_DEFAULT);
		if (null == algorithm01) {
			algorithm01 = new Algorithm();
			algorithm01.setName(StrategyConstant.ALGORITHM_ABSTRACT_DEFAULT);
			algorithm01.setDisplayName("默认");
			algorithm01.setHasDependency(1);
			algorithm01.setFunctionId(abstractFunction.getId());

			algorithmRepository.save(algorithm01);
		}

		Algorithm algorithm02 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_SUMMARY);
		if (null == algorithm02) {
			algorithm02 = new Algorithm();
			algorithm02.setName(StrategyConstant.ALGORITHM_SUMMARY);
			algorithm02.setDisplayName("摘要");
			algorithm02.setHasDependency(1);
			algorithm02.setFunctionId(abstractFunction.getId());
			algorithmRepository.save(algorithm02);

		}

		Algorithm algorithm03 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_KEYWORDS);
		if (null == algorithm03) {
			algorithm03 = new Algorithm();
			algorithm03.setName(StrategyConstant.ALGORITHM_KEYWORDS);
			algorithm03.setDisplayName("关键词");
			algorithm03.setHasDependency(1);
			algorithm03.setFunctionId(abstractFunction.getId());
			algorithmRepository.save(algorithm03);

		}
	}

	private void initEntityExtractionAlgorithmn() {
		Function entityFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_ENTITYEXTRACTION);

		Algorithm algorithm01 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_ENTITY_DEFAULT);
		if (null == algorithm01) {
			algorithm01 = new Algorithm();
			algorithm01.setName(StrategyConstant.ALGORITHM_ENTITY_DEFAULT);
			algorithm01.setDisplayName("默认");
			algorithm01.setFunctionId(entityFunction.getId());
			algorithm01.setHasDic(1);
			algorithm01.setHasDependency(1);
			algorithmRepository.save(algorithm01);
		}

		Algorithm algorithm02 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_ENTITY_NAMEPLACEORG);
		if (null == algorithm02) {
			algorithm02 = new Algorithm();
			algorithm02.setName(StrategyConstant.ALGORITHM_ENTITY_NAMEPLACEORG);
			algorithm02.setDisplayName("人名;地名;机构名;职务职称");
			algorithm02.setFunctionId(entityFunction.getId());
			algorithm02.setHasDic(1);
			algorithm02.setHasDependency(1);
			algorithmRepository.save(algorithm02);
		}

		Algorithm algorithm03 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_ENTITY_NAME);
		if (null == algorithm03) {
			algorithm03 = new Algorithm();
			algorithm03.setName(StrategyConstant.ALGORITHM_ENTITY_NAME);
			algorithm03.setDisplayName("人名");
			algorithm03.setFunctionId(entityFunction.getId());
			algorithm03.setHasDic(1);
			algorithm03.setHasDependency(1);
			algorithmRepository.save(algorithm03);
		}

		Algorithm algorithm04 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_ENTITY_PLACE);
		if (null == algorithm04) {
			algorithm04 = new Algorithm();
			algorithm04.setName(StrategyConstant.ALGORITHM_ENTITY_PLACE);
			algorithm04.setDisplayName("地名");
			algorithm04.setFunctionId(entityFunction.getId());
			algorithm04.setHasDic(1);
			algorithm04.setHasDependency(1);
			algorithmRepository.save(algorithm04);
		}

		Algorithm algorithm05 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_ENTITY_ORG);
		if (null == algorithm05) {
			algorithm05 = new Algorithm();
			algorithm05.setName(StrategyConstant.ALGORITHM_ENTITY_ORG);
			algorithm05.setDisplayName("机构名");
			algorithm05.setFunctionId(entityFunction.getId());
			algorithm05.setHasDic(1);
			algorithm05.setHasDependency(1);
			algorithmRepository.save(algorithm05);
		}
	}

	private void initClassifyAlgorithm() {
		Function classifyFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_CLASSIFY);

		Algorithm algorithm01 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_CLASSIFY_SVM);
		if (null == algorithm01) {
			algorithm01 = new Algorithm();
			algorithm01.setName(StrategyConstant.ALGORITHM_CLASSIFY_SVM);
			algorithm01.setDisplayName("SVM分类");
			algorithm01.setFunctionId(classifyFunction.getId());
			algorithm01.setHasBatch(1);
			algorithm01.setHasModel(1);
			algorithm01.setHasDependency(1);
			algorithmRepository.save(algorithm01);

		}

		Algorithm algorithm02 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_CLASSIFY_MAXENT);
		if (null == algorithm02) {
			algorithm02 = new Algorithm();
			algorithm02.setName(StrategyConstant.ALGORITHM_CLASSIFY_MAXENT);
			algorithm02.setDisplayName("最大熵分类");
			algorithm02.setFunctionId(classifyFunction.getId());
			algorithm02.setHasBatch(1);
			algorithm02.setHasModel(1);
			algorithm02.setHasDependency(1);
			algorithmRepository.save(algorithm02);

		}

		Algorithm algorithm03 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_CLASSIFY_DECISIONTREE);
		if (null == algorithm03) {
			algorithm03 = new Algorithm();
			algorithm03.setName(StrategyConstant.ALGORITHM_CLASSIFY_DECISIONTREE);
			algorithm03.setDisplayName("决策树分类");
			algorithm03.setFunctionId(classifyFunction.getId());
			algorithm03.setHasBatch(1);
			algorithm03.setHasModel(1);
			algorithm03.setHasDependency(1);
			algorithmRepository.save(algorithm03);

		}

		Algorithm algorithm04 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_CLASSIFY_BAYES);
		if (null == algorithm04) {
			algorithm04 = new Algorithm();
			algorithm04.setName(StrategyConstant.ALGORITHM_CLASSIFY_BAYES);
			algorithm04.setDisplayName("贝叶斯分类");
			algorithm04.setFunctionId(classifyFunction.getId());
			algorithm04.setHasBatch(1);
			algorithm04.setHasModel(1);
			algorithm04.setHasDependency(1);
			algorithmRepository.save(algorithm04);

		}

		Algorithm algorithm05 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_CLASSIFY_ADABOOST);
		if (null == algorithm05) {
			algorithm05 = new Algorithm();
			algorithm05.setName(StrategyConstant.ALGORITHM_CLASSIFY_ADABOOST);
			algorithm05.setDisplayName("Adaboost分类");
			algorithm05.setFunctionId(classifyFunction.getId());
			algorithm05.setHasBatch(1);
			algorithm05.setHasModel(1);
			algorithm05.setHasDependency(1);
			algorithmRepository.save(algorithm05);

		}

		Algorithm algorithm06 = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_CLASSIFY_RULE);
		if (null == algorithm06) {
			algorithm06 = new Algorithm();
			algorithm06.setName(StrategyConstant.ALGORITHM_CLASSIFY_RULE);
			algorithm06.setDisplayName("规则分类");
			algorithm06.setFunctionId(classifyFunction.getId());
			algorithm06.setHasBatch(1);
			algorithm06.setHasModel(1);
			algorithm06.setHasRule(1);
			algorithm06.setHasDependency(1);
			algorithmRepository.save(algorithm06);

		}
	}

	private void initEventAlgorithmn() {
		Function eventFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_EVENTEXTRACTION);

		List<Algorithm> algorithms = new ArrayList<>();

		Algorithm algorithm = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_EVENT_DEFAULT);
		if (null == algorithm) {
			algorithm = new Algorithm();
			algorithm.setName(StrategyConstant.ALGORITHM_EVENT_DEFAULT);
			algorithm.setDisplayName("默认");
			algorithm.setFunctionId(eventFunction.getId());
			algorithm.setHasModel(1);
			algorithm.setHasDependency(1);
			algorithms.add(algorithm);
		}

		algorithmRepository.save(algorithms);
	}

	private void initSentimentAlgorithm() {
		Function sentimentFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_SENTIMENTANALYSIS);

		Algorithm algorithm = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_SENTIMENTANALYSIS_DEFAULT);
		if (null == algorithm) {
			algorithm = new Algorithm();
			algorithm.setName(StrategyConstant.ALGORITHM_SENTIMENTANALYSIS_DEFAULT);
			algorithm.setDisplayName("默认");
			algorithm.setFunctionId(sentimentFunction.getId());
			algorithm.setHasDic(1);
			algorithm.setHasRule(1);
			algorithm.setHasDependency(1);
			algorithmRepository.save(algorithm);

		}
	}

	private void initNewwordAlgorithm() {
		Function newwordFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_NEWWORD);

		List<Algorithm> algorithms = new ArrayList<>();

		Algorithm algorithm = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_NEWWORD_DEFAULT);
		if (null == algorithm) {
			algorithm = new Algorithm();
			algorithm.setName(StrategyConstant.ALGORITHM_NEWWORD_DEFAULT);
			algorithm.setDisplayName("默认");
			algorithm.setFunctionId(newwordFunction.getId());
			algorithm.setHasBatch(1);
			algorithm.setHasDependency(1);
			algorithms.add(algorithm);
		}

		algorithmRepository.save(algorithms);
	}

	private void initWord2VecAlgorithm() {
		Function w2vFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_WORD2VEC);

		Algorithm algorithmGoogle = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_WORD2VEC_GOOGLE);
		if (null == algorithmGoogle) {
			algorithmGoogle = new Algorithm();
			algorithmGoogle.setName(StrategyConstant.ALGORITHM_WORD2VEC_GOOGLE);
			algorithmGoogle.setDisplayName("google");
			algorithmGoogle.setFunctionId(w2vFunction.getId());
			algorithmGoogle.setHasModel(1);
			algorithmRepository.save(algorithmGoogle);
		}

		Algorithm algorithmJava = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_WORD2VEC_JAVA);
		if (null == algorithmJava) {
			algorithmJava = new Algorithm();
			algorithmJava.setName(StrategyConstant.ALGORITHM_WORD2VEC_JAVA);
			algorithmJava.setDisplayName("java");
			algorithmJava.setHasModel(1);
			algorithmJava.setFunctionId(w2vFunction.getId());
			algorithmRepository.save(algorithmJava);
		}
	}

	private void initClusterAlgorithm() {
		Function clusteeFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_CLUSTER);

		Algorithm algorithm = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_CLUSTER_DEFAULT);
		if (null == algorithm) {
			algorithm = new Algorithm();
			algorithm.setName(StrategyConstant.ALGORITHM_CLUSTER_DEFAULT);
			algorithm.setDisplayName("默认");
			algorithm.setFunctionId(clusteeFunction.getId());
			algorithm.setHasBatch(1);
			algorithm.setHasDic(1);
			algorithm.setHasDependency(1);
			algorithmRepository.save(algorithm);

		}
	}

	private void initPinyinAlgorithm() {
		Function newwordFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_PINYIN);

		List<Algorithm> algorithms = new ArrayList<>();

		Algorithm algorithm = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_PINYIN_DEFAULT);
		if (null == algorithm) {
			algorithm = new Algorithm();
			algorithm.setName(StrategyConstant.ALGORITHM_PINYIN_DEFAULT);
			algorithm.setDisplayName("默认");
			algorithm.setFunctionId(newwordFunction.getId());
			algorithm.setHasDic(1);
			algorithm.setHasDependency(1);
			algorithms.add(algorithm);
		}

		algorithmRepository.save(algorithms);
	}

	private void initCouplingAlgorithm() {
		Function clusteeFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_COUPLING);
		
		Algorithm algorithm = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_COUPLING_DEFAULT);
		if (null == algorithm) {
			algorithm = new Algorithm();
			algorithm.setName(StrategyConstant.ALGORITHM_COUPLING_DEFAULT);
			algorithm.setDisplayName("默认");
			algorithm.setFunctionId(clusteeFunction.getId());
			algorithm.setHasDic(1);
			algorithm.setHasDependency(1);
			algorithmRepository.save(algorithm);
		}
	}

	@Override
	public void initDefaultStrategy() {
		String[] defaultStrategyNames = new String[] { StrategyConstant.SEGMENT_DEFAULT_STRATEGY,
				StrategyConstant.ENTITY_DEFAULT_STRATEGY };
		List<Strategy> strategies = new ArrayList<>();
		for (String strategyName : defaultStrategyNames) {

			if (!strategyMgmtSevice.validateStrategyExists(strategyName)) {
				Strategy strategy = new Strategy();
				strategy.setName(strategyName);

				strategy.setCreateTime(new Date());

				strategy.setOperation(StrategyConstant.STRATEGY_TYPE_SYSTEM);
				strategy.setDefaultUse(1);
				strategy.setInUsing(0);

				Function function = null;
				Algorithm algorithm = null;
				switch (strategyName) {
				case StrategyConstant.SEGMENT_DEFAULT_STRATEGY:
					function = funcitonRepository.findByName(StrategyConstant.FUNCTION_SEGMENT);
					algorithm = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_SEGMENT_NLP);
					break;

				case StrategyConstant.ENTITY_DEFAULT_STRATEGY:
					function = funcitonRepository.findByName(StrategyConstant.FUNCTION_ENTITYEXTRACTION);
					algorithm = algorithmRepository.findOneByName(StrategyConstant.ALGORITHM_ENTITY_DEFAULT);
					break;

				default:
					break;
				}

				if (null == function || null == algorithm) {
					continue;
				}

				strategy.setFunctionId(function.getId());
				strategy.setAlgorithmId(algorithm.getId());

				strategies.add(strategy);
			}

		}

		strategyRepository.save(strategies);
	}

	@Override
	public void initFunctionDependency() {
		this.initAbsDependFun();
		this.initEntityDepenFun();
		this.initClassifyDependFun();
		this.initSentimentDependFun();
	}

	/**
	 * 初始化情感分析依赖自动分词
	 */
	private void initSentimentDependFun() {
		Function segmentFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_SEGMENT);
		Function sentimentFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_SENTIMENTANALYSIS);
		FunctionDependFunction funDependsFunRelation = functionDependFunctionRepository
				.findByFunctionId(sentimentFunction.getId());

		if (null == funDependsFunRelation) {
			funDependsFunRelation = new FunctionDependFunction();
			funDependsFunRelation.setFunctionId(sentimentFunction.getId());
			funDependsFunRelation.setDependFunctionId(segmentFunction.getId());
			functionDependFunctionRepository.save(funDependsFunRelation);
		}
	}

	/**
	 * 初始化文本分类依赖自动分词
	 */
	private void initClassifyDependFun() {
		Function segmentFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_SEGMENT);
		Function classifyFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_CLASSIFY);
		FunctionDependFunction funDependsFunRelation = functionDependFunctionRepository
				.findByFunctionId(classifyFunction.getId());

		if (null == funDependsFunRelation) {
			funDependsFunRelation = new FunctionDependFunction();
			funDependsFunRelation.setFunctionId(classifyFunction.getId());
			funDependsFunRelation.setDependFunctionId(segmentFunction.getId());
			functionDependFunctionRepository.save(funDependsFunRelation);
		}
	}

	/**
	 * 初始化实体抽取依赖自动分词
	 */
	private void initEntityDepenFun() {
		Function segmentFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_SEGMENT);
		Function entityFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_ENTITYEXTRACTION);
		FunctionDependFunction funDependsFunRelation = functionDependFunctionRepository
				.findByFunctionId(entityFunction.getId());

		if (null == funDependsFunRelation) {
			funDependsFunRelation = new FunctionDependFunction();
			funDependsFunRelation.setFunctionId(entityFunction.getId());
			funDependsFunRelation.setDependFunctionId(segmentFunction.getId());
			functionDependFunctionRepository.save(funDependsFunRelation);
		}
	}

	/**
	 * 初始化摘要关键词功能依赖自动分词
	 */
	private void initAbsDependFun() {
		Function segmentFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_SEGMENT);
		Function abstractFunction = funcitonRepository.findByName(StrategyConstant.FUNCTION_ABSTRACT);
		FunctionDependFunction funDependsFunRelation = functionDependFunctionRepository
				.findByFunctionId(abstractFunction.getId());

		if (null == funDependsFunRelation) {
			funDependsFunRelation = new FunctionDependFunction();
			funDependsFunRelation.setFunctionId(abstractFunction.getId());
			funDependsFunRelation.setDependFunctionId(segmentFunction.getId());
			functionDependFunctionRepository.save(funDependsFunRelation);
		}
	}

}
