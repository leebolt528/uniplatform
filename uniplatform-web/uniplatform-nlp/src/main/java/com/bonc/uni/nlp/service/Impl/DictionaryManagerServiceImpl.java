package com.bonc.uni.nlp.service.Impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.config.PosConfig;
import com.bonc.uni.nlp.dao.DicFuncRelationRepository;
import com.bonc.uni.nlp.dao.DicSubTypeForTypeRepository;
import com.bonc.uni.nlp.dao.DicSubTypeFunctionRelationRepository;
import com.bonc.uni.nlp.dao.DicSubTypeRepository;
import com.bonc.uni.nlp.dao.DicTypeRepository;
import com.bonc.uni.nlp.dao.DictionaryRepository;
import com.bonc.uni.nlp.dao.FieldTypeRepository;
import com.bonc.uni.nlp.dao.FuncitonRepository;
import com.bonc.uni.nlp.dao.WordMapRepository;
import com.bonc.uni.nlp.dao.WordOrdinaryRepository;
import com.bonc.uni.nlp.dao.WordSentimentRepository;
import com.bonc.uni.nlp.dao.label.LabelRepository;
import com.bonc.uni.nlp.dao.label.LabelWordRelationRepository;
import com.bonc.uni.nlp.entity.dic.DicFuncRelation;
import com.bonc.uni.nlp.entity.dic.DicSubType;
import com.bonc.uni.nlp.entity.dic.DicType;
import com.bonc.uni.nlp.entity.dic.Dictionary;
import com.bonc.uni.nlp.entity.dic.FieldType;
import com.bonc.uni.nlp.entity.dic.Function;
import com.bonc.uni.nlp.entity.dic.WordMap;
import com.bonc.uni.nlp.entity.dic.WordOrdinary;
import com.bonc.uni.nlp.entity.dic.WordSentiment;
import com.bonc.uni.nlp.entity.label.Label;
import com.bonc.uni.nlp.entity.label.LabelWordRelation;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.IDictionaryManagementService;
import com.bonc.uni.nlp.utils.Encoding;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @ClassName:DictionaryManagerServiceImpl
 * @Package:com.bonc.text.service.impl
 * @Description:TODO
 * @author:Chris
 * @date:2017年4月12日 下午4:45:35
 */
@Service
public class DictionaryManagerServiceImpl implements IDictionaryManagementService {

	@Autowired
	WordMapRepository wordMapRepository;
	@Autowired
	WordOrdinaryRepository wordOrdinaryRepository;
	@Autowired
	WordSentimentRepository wordSentimentRepository;
	@Autowired
	DictionaryRepository dictRepository;
	@Autowired
	DicSubTypeRepository dicSubTypeRepository;
	@Autowired
	DicFuncRelationRepository dicFuncRelationRepository;
	@Autowired
	FuncitonRepository functionRepository;
	@Autowired
	DicTypeRepository dicTypeRepository;
	@Autowired
	DicSubTypeFunctionRelationRepository dicSubTypeFunctionRelationRepository;
	@Autowired
	DicSubTypeForTypeRepository dicSubTypeForTypeRepository;
	@Autowired
	FieldTypeRepository fieldTypeRepository;
	@Autowired
	DictionaryRepository dictionaryRepository;
	@Autowired
	LabelRepository labelRepository;
	@Autowired
	LabelWordRelationRepository labelWordRelationRepository;

	Sort sort = new Sort(Sort.Direction.DESC, "createTime");
	Sort indexsort = new Sort(Sort.Direction.DESC, "index");

	// private static IRedisHandler redisHandler;
	//
	// static {
	// RedisManager.getInstance().initStandaloneHandlerConfig(RedisConfig.REDIS_HOST,
	// RedisConfig.REDIS_PORT,
	// RedisConfig.REDIS_PASSWORD, RedisConfig.REDIS_MAX_TOTAL,
	// RedisConfig.REDIS_MAX_IDLE,
	// RedisConfig.REDIS_MIN_IDLE, RedisConfig.REDIS_MAX_WAIT_MILLIS,
	// RedisConfig.REDIS_CONNECTION_TIMEOUT,
	// RedisConfig.REDIS_TIMEOUT, RedisConfig.REDIS_TEST_ON_BORROW);
	//
	// redisHandler = RedisManager.getInstance().getStandaloneHandler();
	//
	// }

	@Override
	public List<Map<String, Object>> intoAddDictionary() {
		List<Map<String, Object>> subTypeFromTypes = new ArrayList<>();
		List<DicType> dicTypeAll = dicTypeRepository.findAll();
		for (DicType dicType : dicTypeAll) {
			Map<String, Object> subTypeFromType = new HashMap<>();

			subTypeFromType.put("type", dicType);
			List<String> subTypIds = dicSubTypeForTypeRepository.findSubTypeIdByTypeIds(dicType.getId());

			List<DicSubType> subTypeAll = dicSubTypeRepository.findAll(subTypIds);
			Comparator<DicSubType> subTypeComparator = new Comparator<DicSubType>() {
				public int compare(DicSubType s1, DicSubType s2) {

					if (s1.getIndex() > s2.getIndex()) {
						return -1;
					}
					return 1;
				}
			};
			Collections.sort(subTypeAll, subTypeComparator);
			List<Map<String, Object>> ordinarySubTypeAll = new ArrayList<>();
			for (DicSubType dicSubType : subTypeAll) {
				Map<String, Object> subtypeToFun = new HashMap<>();
				String id = dicSubType.getId();
				List<String> findFunctionIds = dicSubTypeFunctionRelationRepository.findFunctionIds(id);
				List<Function> function = functionRepository.findAll(findFunctionIds);
				Comparator<Function> functionComparator = new Comparator<Function>() {
					public int compare(Function s1, Function s2) {

						if (s1.getIndex() > s2.getIndex()) {
							return -1;
						}
						return 1;
					}
				};
				Collections.sort(function, functionComparator);
				subtypeToFun.put("subType", dicSubType);
				subtypeToFun.put("function", function);
				ordinarySubTypeAll.add(subtypeToFun);
			}
			subTypeFromType.put("subTypes", ordinarySubTypeAll);

			subTypeFromTypes.add(subTypeFromType);
		}
		return subTypeFromTypes;
	}

	@Override
	@Transactional
	public int addDictionary(String dictName, String dictTypeId, String dicSubTypeId, List<WordOrdinary> wordOrdinarys,
			List<WordMap> wordMaps, List<WordSentiment> wordSentiments, String format, String functionId) {
		// CurrentUserUtils.getInstance().getUser().getId()
		Dictionary existedDict = dictRepository.findByName(dictName);
		if (null != existedDict) {
			return -1;
		}

		Dictionary dictionary = new Dictionary();

		dictionary.setName(dictName);
		// 创建词库
		dictionary.setDicTypeId(dictTypeId);
		dictionary.setDicSubTypeId(dicSubTypeId);
		Date creatTime = new Date();
		dictionary.setCreateTime(creatTime);
		// dictionary.setUserId(CurrentUserUtils.getInstance().getUser().getId());
		dictionary.setFormat(format);
		dictionary.setStatus(0);
		Dictionary addedDict = dictRepository.save(dictionary);
		String dicId = addedDict.getId();

		// 词典标签关系表添加数据
		String[] functionIds = null;

		if (!StringUtil.isEmpty(functionId)) {
			functionIds = functionId.split(",");
			List<DicFuncRelation> dicFuncRelations = new ArrayList<>();
			for (String functionid : functionIds) {
				DicFuncRelation dicFuncRelation = new DicFuncRelation();
				dicFuncRelation.setDicId(dicId);
				dicFuncRelation.setFunctionId(functionid);
				dicFuncRelation.setStatus(0);
				dicFuncRelations.add(dicFuncRelation);
			}
			if (!dicFuncRelations.isEmpty()) {

				dicFuncRelationRepository.save(dicFuncRelations);
				addedDict.setStatus(1);
				dictRepository.save(addedDict);
			}

		}

		// 根据词库类型添加词
		List<WordMap> wordmapList = new ArrayList<>();
		List<WordOrdinary> ordinarys = new ArrayList<>();
		List<WordSentiment> sentiments = new ArrayList<>();
		if (!StringUtil.isEmpty(addedDict.getId())) {
			if (null != wordMaps && "map".equals(format)) {

				for (WordMap wordMap : wordMaps) {
					wordMap.setDictionaryId(dicId);
					wordmapList.add(wordMap);

				}
				wordMapRepository.save(wordmapList);
			}
			if (null != wordOrdinarys && "ordinary".equals(format)) {

				for (WordOrdinary wordOrdinary : wordOrdinarys) {
					wordOrdinary.setDictionaryId(dicId);
					ordinarys.add(wordOrdinary);
				}
				wordOrdinaryRepository.save(ordinarys);
			}
			if (null != wordSentiments && "sentiment".equals(format)) {

				for (WordSentiment wordOrdinary : wordSentiments) {
					wordOrdinary.setDictionaryId(dicId);
					sentiments.add(wordOrdinary);
				}
				wordSentimentRepository.save(sentiments);
			}
		}
		/**
		 * 在redis中添加词
		 */
		//
		// if (wordmapList != null && wordmapList.size() > 0) {
		// for (WordMap addedWord : wordmapList) {
		// String key = DictionaryConstant.REDIS_WORD_MAP_PREFIX + dictName +
		// "_" + addedWord.getWordKey();
		// Map<String, String> wordMap = new HashMap<>();
		// wordMap.put(DictionaryConstant.REDIS_WORD_MAP_KEY,
		// StringUtil.avoidNull(addedWord.getWordKey()));
		// wordMap.put(DictionaryConstant.REDIS_WORD_MAP_VALUE,
		// StringUtil.avoidNull(addedWord.getWordValue()));
		//
		// redisHandler.hmset(key, wordMap);
		// }
		// }
		// if (ordinarys != null && ordinarys.size() > 0) {
		// for (WordOrdinary addedWord : ordinarys) {
		// String key = DictionaryConstant.REDIS_WORD_PREFIX + dictName + "_" +
		// addedWord.getWord();
		// Map<String, String> wordMap = new HashMap<>();
		// wordMap.put(DictionaryConstant.REDIS_WORD_NAME,
		// StringUtil.avoidNull(addedWord.getWord()));
		// wordMap.put(DictionaryConstant.REDIS_WORD_NATURE,
		// StringUtil.avoidNull(addedWord.getNature()));
		// wordMap.put(DictionaryConstant.REDIS_WORD_FREQUENCY,
		// String.valueOf(addedWord.getFrequency()));
		// redisHandler.hmset(key, wordMap);
		// }
		//
		// }
		return 1;
	}

	@Override
	@Transactional
	public boolean removeDictionary(String dictId) {
		Dictionary existedDict = dictRepository.findOne(dictId);
		if (null == existedDict)
			throw new AdminException("删除失败，该词库不存在");
		if (2 == existedDict.getStatus()) {
			throw new AdminException("删除失败，该词库标签已被启用");
		}

		// 单独考虑领域词库的情况
		String fieldTypeId = existedDict.getFieldTypeId();
		if (!StringUtil.isEmpty(fieldTypeId)) {
			List<WordOrdinary> wordOrdinaries = wordOrdinaryRepository.findAllByDictionaryId(dictId);
			List<String> wordIds = new ArrayList<>();
			for (WordOrdinary wordOrdinary : wordOrdinaries) {
				wordIds.add(wordOrdinary.getId());
			}
			// 删除该词库中的词
			if (wordOrdinaries != null && wordOrdinaries.size() != 0) {
				wordOrdinaryRepository.delete(wordOrdinaries);
			}
			if (wordIds.size() > 0) {
				List<LabelWordRelation> labelWordRelations = labelWordRelationRepository.findAllByWordIdIn(wordIds);
				List<String> labelIds = new ArrayList<>();
				if (labelWordRelations.size() > 0) {
					for (LabelWordRelation labelWordRelation : labelWordRelations) {
						labelIds.add(labelWordRelation.getLabelId());
						labelWordRelationRepository.delete(labelWordRelation);
					}
					// 修改未被其他词库使用的标签的状态
					for (String labelId : labelIds) {
						List<LabelWordRelation> labelWordRelationsExist = labelWordRelationRepository
								.findAllByLabelId(labelId);
						if (0 == labelWordRelationsExist.size()) {
							Label label = labelRepository.findOne(labelId);
							label.setStatus(0);
							labelRepository.save(label);
						}
					}
				}
			}
		}
		String format = existedDict.getFormat();
		String dicName = existedDict.getName();
		String dicTypeId = existedDict.getDicSubTypeId();
		List<WordMap> wordMap = new ArrayList<>();
		List<WordOrdinary> wordOrdinary = new ArrayList<>();
		List<WordSentiment> wordSentiment = new ArrayList<>();
		wordMap = wordMapRepository.findAllByDictionaryId(dictId);
		wordOrdinary = wordOrdinaryRepository.findAllByDictionaryId(dictId);
		wordSentiment = wordSentimentRepository.findAllByDictionaryId(dictId);
		DicSubType subType = dicSubTypeRepository.findOne(dicTypeId);
		String dicSubclassName = subType.getName();
		/**
		 * 删除redis中的所有该词库中的词
		 */
		dictRepository.delete(dictId);
		String dictName = existedDict.getName();
		if (format == "map" && wordMap != null && wordMap.size() != 0) {
			List<WordMap> delMapWords = wordMap;
			List<String> delWordKeys = new ArrayList<>();
			if (delMapWords != null && delMapWords.size() != 0) {
				wordMapRepository.delete(delMapWords);
			}
			// or (WordMap word : delMapWords) {
			// String wordKey = DictionaryConstant.REDIS_WORD_PREFIX + dictName
			// + "_" + word.getWordKey();
			// delWordKeys.add(wordKey);
			// }
			// if (!delWordKeys.isEmpty()) {
			// String[] delKyes = new String[delWordKeys.size()];
			// redisHandler.del(delWordKeys.toArray(delKyes));
			// }
		}
		// 非领域词库
		if ("ordinary".equals(format) && wordOrdinary != null && wordOrdinary.size() != 0
				&& StringUtil.isEmpty(fieldTypeId)) {
			List<WordOrdinary> delOrdinaryWords = wordOrdinary;
			List<String> delWordKeys = new ArrayList<>();
			if (delOrdinaryWords != null && delOrdinaryWords.size() != 0) {
				wordOrdinaryRepository.delete(delOrdinaryWords);
			}
			// for (WordOrdinary word : delOrdinaryWords) {
			// String wordKey = DictionaryConstant.REDIS_WORD_PREFIX + dictName
			// + "_" + word.getWord();
			// delWordKeys.add(wordKey);
			// }
			// if (!delWordKeys.isEmpty()) {
			// String[] delKyes = new String[delWordKeys.size()];
			// redisHandler.del(delWordKeys.toArray(delKyes));
			// }
		}
		if ("sentiment".equals(format) && wordSentiment != null && wordSentiment.size() != 0) {
			List<WordSentiment> delOrdinaryWords = wordSentiment;
			if (delOrdinaryWords != null && delOrdinaryWords.size() != 0) {
				wordSentimentRepository.delete(delOrdinaryWords);
			}
		}

		// 查询所有引用该词库的功能
		List<DicFuncRelation> relations = dicFuncRelationRepository.findAllByDicId(dictId);
		List<DicFuncRelation> relationAll = new ArrayList<>();
		if (!relations.isEmpty()) {
			for (DicFuncRelation relation : relations) {
				// 对功能停用词库并解除关联
				if (null == relation || relation.getStatus() == 0) {
					relationAll.add(relation);
					continue;
				}
				// redis 停用
				/**
				 * mysql中停用
				 */
				relation.setStatus(0);
				relationAll.add(relation);
			}
			if (relationAll != null || relationAll.size() != 0) {
				dicFuncRelationRepository.save(relationAll);
				dicFuncRelationRepository.delete(relationAll);
			}
		}

		return true;
	}

	/**
	 * 修改词库名称
	 * 
	 * @param dictId
	 * @param newDictName
	 * @return
	 */
	@Override
	public int updateDictionaryInfo(String dictId, String newDictName) {
		Dictionary existedDictByName = dictRepository.findByName(newDictName);
		if (existedDictByName != null) {
			return 0;
		}
		Dictionary existedDict = dictRepository.findOne(dictId);
		if (null == existedDict)
			return 0;
		existedDict.setName(newDictName);
		dictRepository.save(existedDict);
		return 1;
	}

	/**
	 * 查询词典的所有信息
	 * 
	 * @param dic
	 * @return
	 */
	private Map<String, Object> dicInfo(Dictionary dic) {

		Map<String, Object> rsMap = new HashMap<>();
		String subTypeName = null;
		String typeName = null;
		DicSubType dicSubType = dicSubTypeRepository.findOneById(dic.getDicSubTypeId());
		if (null != dicSubType) {
			subTypeName = dicSubType.getDisPlayName();
		}
		DicType dicType = dicTypeRepository.findOneById(dic.getDicTypeId());
		if (null != dicType) {
			typeName = dicType.getDisplayName();
		}
		String dicFormat = dic.getFormat();
		Integer dicStatus = dic.getStatus();
		rsMap.put("format", dicFormat);
		try {
			switch (dicFormat) {
			case "map":
				List<WordMap> wordMap = wordMapRepository.findAllByDictionaryId(dic.getId());
				if (wordMap == null || wordMap.isEmpty()) {
					rsMap.put("wordNum", 0);
				} else {

					rsMap.put("wordNum", String.valueOf(wordMap.size()));
				}
				break;
			case "ordinary":
				List<WordOrdinary> wordOrdinary = wordOrdinaryRepository.findAllByDictionaryId(dic.getId());

				if (wordOrdinary == null || wordOrdinary.isEmpty()) {
					rsMap.put("wordNum", 0);
				} else {

					rsMap.put("wordNum", String.valueOf(wordOrdinary.size()));
				}
				break;
			case "sentiment":
				List<WordSentiment> wordSentiment = wordSentimentRepository.findAllByDictionaryId(dic.getId());

				if (wordSentiment == null || wordSentiment.isEmpty()) {
					rsMap.put("wordNum", 0);
				} else {

					rsMap.put("wordNum", String.valueOf(wordSentiment.size()));
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		}

		List<Map<String, String>> functions = new ArrayList<>();
		List<DicFuncRelation> dicFuncRelations = new ArrayList<>();
		dicFuncRelations = dicFuncRelationRepository.findAllByDicId(dic.getId());
		if (dicFuncRelations != null && dicFuncRelations.size() != 0) {

			for (DicFuncRelation dicFuncRelation : dicFuncRelations) {

				Map<String, String> tempMap = new HashMap<>();
				String functionName = functionRepository.findOne(dicFuncRelation.getFunctionId()).getDisplayName();
				String functionId = functionRepository.findOne(dicFuncRelation.getFunctionId()).getId();

				Integer status = dicFuncRelation.getStatus();
				tempMap.put("function", functionName);
				tempMap.put("status", String.valueOf(status));
				tempMap.put("functionId", functionId);

				functions.add(tempMap);
			}
		}

		rsMap.put("function", functions);

		rsMap.put("dicId", dic.getId());

		rsMap.put("dicName", dic.getName());

		rsMap.put("subTypeName", subTypeName);

		rsMap.put("format", dic.getFormat());

		rsMap.put("typeName", typeName);
		rsMap.put("dicStatus", dicStatus);

		return rsMap;

	}

	/**
	 * 查询词库
	 * 
	 * @param dicTypeId
	 *            词库类型
	 * @param functionId
	 *            功能类型
	 * @return
	 */
	private List<Map<String, Object>> queryDic(String dicTypeId, String functionId) {
		List<Map<String, Object>> rsList = new ArrayList<>();
		List<Dictionary> dictionary = dictRepository.findAllByDicTypeId(dicTypeId, sort);
		if (dictionary.isEmpty()) {
			return rsList;
		}

		if (null == functionId) {
			for (Dictionary dic : dictionary) {

				Map<String, Object> rsMap = dicInfo(dic);

				rsList.add(rsMap);
			}
		} else {
			List<Dictionary> newDictionary = new ArrayList<>();
			for (Dictionary dic : dictionary) {
				String dicId = dic.getId();
				if (null != dicFuncRelationRepository.findOneByDicIdAndFunctionId(dicId, functionId)) {
					newDictionary.add(dic);
				}
			}
			for (Dictionary dic : newDictionary) {
				Map<String, Object> rsMap = dicInfo(dic);
				rsList.add(rsMap);
			}
		}
		return rsList;
	}

	@Override
	public List<Map<String, Object>> queryDicByType(String dicTypeId) {
		return queryDic(dicTypeId, null);
	}

	@Override
	public List<Map<String, Object>> queryDicByTypeAndFunction(String dicTypeId, String functionId) {
		return queryDic(dicTypeId, functionId);
	}

	@Override
	public List<Map<String, Object>> queryAllDic() {
		List<Map<String, Object>> rsList = new ArrayList<>();
		List<Dictionary> dictionaries = dictRepository.findAll(sort);
		if (dictionaries.isEmpty()) {
			return rsList;
		}
		for (Dictionary dictionary : dictionaries) {
			Map<String, Object> rsMap = dicInfo(dictionary);
			rsList.add(rsMap);
		}

		return rsList;
	}

	/**
	 * 搜索词库
	 */
	@Override
	public List<Map<String, Object>> queryAllDicByKeyWord(String dicTypeId, String keyWord) {
		List<Map<String, Object>> result = new ArrayList<>();
		if (StringUtil.isEmpty(dicTypeId)) {
			keyWord = "%" + keyWord + "%";
			List<Dictionary> content = dictRepository.findAllByName(keyWord);
			if (content != null && content.size() != 0) {
				for (Dictionary dictionary : content) {
					Map<String, Object> dicInfo = dicInfo(dictionary);
					result.add(dicInfo);
				}
			}
			// 查询词库中词包含name
			Set<String> dicIds = new HashSet<>();
			List<WordOrdinary> wordOrdinarys = wordOrdinaryRepository.findAllByName(keyWord);
			if (wordOrdinarys != null && wordOrdinarys.size() != 0) {
				for (WordOrdinary wordOrdinary : wordOrdinarys) {
					dicIds.add(wordOrdinary.getDictionaryId());
				}
			}
			List<WordMap> wordMaps = wordMapRepository.findAllByName(keyWord);
			if (wordMaps != null && wordMaps.size() != 0) {
				for (WordMap wordMap : wordMaps) {
					dicIds.add(wordMap.getDictionaryId());
				}
			}
			List<WordSentiment> wordSentiments = wordSentimentRepository.findAllByName(keyWord);
			if (wordSentiments != null && wordSentiments.size() != 0) {
				for (WordSentiment wordSentiment : wordSentiments) {
					dicIds.add(wordSentiment.getDictionaryId());
				}
			}
			List<Dictionary> findAll = dictRepository.findAll(dicIds);
			findAll.removeAll(content);
			for (Dictionary dictionary : findAll) {
				Map<String, Object> dicInfo = dicInfo(dictionary);
				result.add(dicInfo);
			}
		} else {
			// 按词典类型查询
			keyWord = "%" + keyWord + "%";
			List<Dictionary> content = dictRepository.findAllByNameAndDicTypeId(keyWord, dicTypeId);
			for (Dictionary dictionary : content) {
				Map<String, Object> dicInfo = dicInfo(dictionary);
				result.add(dicInfo);
			}
			// 查询词库中词包含name
			Set<String> dicIds = new HashSet<>();
			List<WordOrdinary> wordOrdinarys = wordOrdinaryRepository.findAllByName(keyWord);
			if (wordOrdinarys != null && wordOrdinarys.size() != 0) {
				for (WordOrdinary wordOrdinary : wordOrdinarys) {
					String dicId = wordOrdinary.getDictionaryId();
					Dictionary dictionary = dictionaryRepository.findOne(dicId);
					if (dicTypeId.equals(dictionary.getDicTypeId())) {
						if (!dicIds.contains(dicId)) {
							dicIds.add(dicId);
						}
					}
				}
			}
			List<WordMap> wordMaps = wordMapRepository.findAllByName(keyWord);
			if (wordMaps != null && wordMaps.size() != 0) {
				for (WordMap wordMap : wordMaps) {
					String dicId = wordMap.getDictionaryId();
					Dictionary dictionary = dictionaryRepository.findOne(dicId);
					if (dicTypeId.equals(dictionary.getDicTypeId())) {
						if (!dicIds.contains(dicId)) {
							dicIds.add(dicId);
						}
					}
				}
			}
			List<WordSentiment> wordSentiments = wordSentimentRepository.findAllByName(keyWord);
			if (wordSentiments != null && wordSentiments.size() != 0) {
				for (WordSentiment wordSentiment : wordSentiments) {
					String dicId = wordSentiment.getDictionaryId();
					Dictionary dictionary = dictionaryRepository.findOne(dicId);
					if (dicTypeId.equals(dictionary.getDicTypeId())) {
						if (!dicIds.contains(dicId)) {
							dicIds.add(dicId);
						}
					}
				}
			}
			for (String dicId : dicIds) {
			}
			List<Dictionary> findAll = dictRepository.findAll(dicIds);
			findAll.removeAll(content);
			for (Dictionary dictionary : findAll) {
				Map<String, Object> dicInfo = dicInfo(dictionary);
				if (!result.contains(dicInfo)) {
					result.add(dicInfo);
				}
			}
		}

		return result;
	}

	@Override
	public int getSearchDictionaryNumber(String name) {
		if (StringUtil.isEmpty(name)) {
			return dictRepository.findAll().size();
		}

		name = "%" + name + "%";

		int searchNumber = dictRepository.countByName(name);

		return searchNumber;

	}

	@Override
	public List<Map<String, Object>> queryDicBySubType(String subTypeId) {
		List<Dictionary> dictionarys = dictRepository.findAllByDicSubTypeId(subTypeId, sort);
		if (dictionarys == null || dictionarys.size() == 0) {
			return null;
		}
		List<Map<String, Object>> result = new ArrayList<>();
		for (Dictionary dictionary : dictionarys) {
			Map<String, Object> rsMap = dicInfo(dictionary);
			result.add(rsMap);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> queryDicByTypeAndSubType(String dicTypeId, String subTypeId) {
		List<Dictionary> dictionarys = dictRepository.findAllByDicTypeIdAndDicSubTypeId(dicTypeId, subTypeId, sort);
		if (dictionarys == null || dictionarys.size() == 0) {
			return null;
		}
		List<Map<String, Object>> result = new ArrayList<>();
		for (Dictionary dictionary : dictionarys) {
			Map<String, Object> rsMap = dicInfo(dictionary);
			result.add(rsMap);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> queryDicByFunction(String functionId) {
		List<String> dicIds = dicFuncRelationRepository.findSubTypeIdByFunctionId(functionId);

		if (dicIds == null || dicIds.size() == 0) {
			return null;
		}
		List<Dictionary> dictionarys = dictRepository.findAll(dicIds);
		if (dictionarys == null || dictionarys.size() == 0) {
			return null;
		}
		List<Map<String, Object>> result = new ArrayList<>();
		for (Dictionary dictionary : dictionarys) {
			Map<String, Object> rsMap = dicInfo(dictionary);
			result.add(rsMap);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> queryDicBySubTypeAndFunctionl(String subTypeId, String functionId) {
		List<String> dicIdBysunType = dictRepository.findDicIdBySubTypeId(subTypeId, sort);

		List<String> dicIdbyFunction = dicFuncRelationRepository.findSubTypeIdByFunctionId(functionId);

		dicIdBysunType.retainAll(dicIdbyFunction);

		if (dicIdBysunType == null || dicIdBysunType.size() == 0) {
			return null;
		}
		List<Dictionary> dictionarys = dictRepository.findAll(dicIdBysunType);
		List<Map<String, Object>> result = new ArrayList<>();
		for (Dictionary dictionary : dictionarys) {
			Map<String, Object> rsMap = dicInfo(dictionary);
			result.add(rsMap);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> queryDicByTypeAndFunctionAndSubType(String dicTypeId, String functionId,
			String subTypeId) {
		List<String> dicIdBysunType = dictRepository.findDicIdBySubTypeIdAndTypeId(subTypeId, dicTypeId, sort);

		List<String> dicIdbyFunction = dicFuncRelationRepository.findSubTypeIdByFunctionId(functionId);
		dicIdBysunType.retainAll(dicIdbyFunction);
		if (dicIdBysunType == null || dicIdBysunType.size() == 0) {
			return null;
		}
		List<Dictionary> dictionarys = dictRepository.findAll(dicIdBysunType);
		List<Map<String, Object>> result = new ArrayList<>();
		for (Dictionary dictionary : dictionarys) {
			Map<String, Object> rsMap = dicInfo(dictionary);
			result.add(rsMap);
		}

		return result;
	}

	@Override
	public List<FieldType> listFieldType(String typeId, String subTypeId) {
		DicType dicType = dicTypeRepository.findOne(typeId);
		List<FieldType> fieldTypes = new ArrayList<>();
		if ("field".equals(dicType.getName())) {
			DicSubType dicSubType = dicSubTypeRepository.findOne(subTypeId);
			if ("entitydictionary".equals(dicSubType.getName())) {
				fieldTypes = fieldTypeRepository.findAllBySubTypeId(subTypeId);
			} else {
				fieldTypes = fieldTypeRepository.findAll();
			}
		}

		return fieldTypes;
	}

	@Override
	public List<Integer> addFieldDictionary(String dicName, String dicTypeid, String dicSubTypeId, String fieldTypeId,
			MultipartFile[] files, String dicFormat, String functionId) {
		Dictionary existedDict = dictRepository.findByName(dicName);
		if (null != existedDict) {
			throw new AdminException("该词典名称已存在，请重新输入");
		}
		Dictionary dictionary = new Dictionary();
		dictionary.setName(dicName);
		// 创建词库
		dictionary.setDicTypeId(dicTypeid);
		dictionary.setDicSubTypeId(dicSubTypeId);
		dictionary.setFieldTypeId(fieldTypeId);
		Date creatTime = new Date();
		dictionary.setCreateTime(creatTime);
		// dictionary.setUserId(CurrentUserUtils.getInstance().getUser().getId());
		dictionary.setFormat(dicFormat);
		dictionary.setStatus(0);
		Dictionary addedDict = dictRepository.save(dictionary);
		String dicId = addedDict.getId();
		int wordAllNum = 0;
		int ordinaryWordSucceedAllNum = 0;
		List<String> existList = new ArrayList<>();
		String valueS = null;
		for (MultipartFile file : files) {
			if (null == file) {
				continue;
			}
			try (InputStream in = file.getInputStream();
					InputStreamReader inReader = new InputStreamReader(in);
					BufferedReader reader = new BufferedReader(inReader)) {
				byte[] b = new byte[3];
				in.read(b);
				if (!"UTF-8".equals(Encoding.tryEncoding(file))) {
					throw new AdminException("请上传Utf-8");
				}
				String line = null;
			  if ("ordinary".equals(dicFormat) && !StringUtil.isEmpty(fieldTypeId)) {
					// 人|标签（比如： 人|东方国信|总经理）-->拆分存储数据库
					// 人存储到ordinary表中，标签部分存储到label表中
					while (null != (line = reader.readLine())) {
						if (StringUtil.isEmpty(line)) {
							continue;
						}
						String wordName = null;
						String labelStr = null;
						wordAllNum += 1;
						Label labelInfo = new Label();
						if (line.startsWith("\uFEFF"))
							line = line.replace("\uFEFF", "");
						String[] infos = line.split(",|，");
						if (infos.length == 3) {
							if ("=".equals(infos[1]) || infos[1].contains("=")) {
								continue;
							}
							if (null == PosConfig.natureNameMap.get(infos[1])) {
								continue;
							}
							try {
								Integer.parseInt(infos[2]);
							} catch (Exception e) {
								continue;
							}
							if (infos[0].contains("|")) {
								int index = infos[0].indexOf("|");
								wordName = infos[0].substring(0, index);
								labelStr = infos[0].substring(index + 1);
							} else {
								wordName = infos[0];
								labelStr = "";
							}
							WordOrdinary wordOrdinaryExist = wordOrdinaryRepository.findByNameAndDicId(wordName,
									addedDict.getId());
							if (!StringUtil.isEmpty(labelStr)) {
								labelInfo = labelRepository.findOneByName(labelStr);
								if (null == labelInfo) {
									labelInfo = new Label();
									labelInfo.setName(labelStr);
									labelInfo.setStatus(1);
									labelInfo.setCreateTime(new Date());
									// label.setUserId(userId);
									labelInfo = labelRepository.save(labelInfo);
								} else {
									labelInfo.setStatus(1);
									labelInfo.setUpdateTime(new Date());
									labelRepository.save(labelInfo);
								}
							}
							if (null == wordOrdinaryExist) {
								WordOrdinary wordOrdinary = new WordOrdinary();
								wordOrdinary.setDictionaryId(dicId);
								wordOrdinary.setWord(wordName);
								wordOrdinary.setNature(infos[1]);
								wordOrdinary.setFrequency(Integer.parseInt(infos[2]));
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(dicId).append(wordName).append(infos[1])
										.append(Integer.parseInt(infos[2]));
								valueS = stringBuilder.toString();
								if (!existList.contains(valueS)) {
									existList.add(valueS);
									try {
										ordinaryWordSucceedAllNum += 1;
										wordOrdinaryRepository.save(wordOrdinary);
										WordOrdinary wOrdinary = wordOrdinaryRepository.findOne(wordOrdinary.getId());

										if (!StringUtil.isEmpty(labelStr)) {
											LabelWordRelation labelWordRelation = new LabelWordRelation();
											labelWordRelation.setLabelId(labelInfo.getId());
											labelWordRelation.setWordId(wOrdinary.getId());
											labelWordRelationRepository.save(labelWordRelation);
										}
									} catch (JpaSystemException e) {
									}
								}
							} else {
								if (!StringUtil.isEmpty(labelStr)) {
									LabelWordRelation labelWordRelation = new LabelWordRelation();
									labelWordRelation.setLabelId(labelInfo.getId());
									labelWordRelation.setWordId(wordOrdinaryExist.getId());
									labelWordRelationRepository.save(labelWordRelation);
								}
							}
						}
						if (infos.length == 1) {
							wordName = infos[0];
							labelStr = "";
							WordOrdinary wordOrdinaryExist = wordOrdinaryRepository.findByNameAndDicId(wordName,
									addedDict.getId());
							if (!StringUtil.isEmpty(labelStr)) {
								labelInfo = labelRepository.findOneByName(labelStr);
								if (null == labelInfo) {
									labelInfo = new Label();
									labelInfo.setName(labelStr);
									labelInfo.setStatus(1);
									labelInfo.setCreateTime(new Date());
									// label.setUserId(userId);
									labelInfo = labelRepository.save(labelInfo);
								} else {
									labelInfo.setStatus(1);
									labelInfo.setUpdateTime(new Date());
									labelRepository.save(labelInfo);
								}
							}
							if (null == wordOrdinaryExist) {
								WordOrdinary wordOrdinary = new WordOrdinary();
								wordOrdinary.setDictionaryId(dicId);
								wordOrdinary.setWord(wordName);
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(dicId).append(wordName).append(0);
								valueS = stringBuilder.toString();
								if (!existList.contains(valueS)) {
									existList.add(valueS);
									try {
										ordinaryWordSucceedAllNum += 1;
										wordOrdinaryRepository.save(wordOrdinary);
										WordOrdinary wOrdinary = wordOrdinaryRepository.findOne(wordOrdinary.getId());
										if (!StringUtil.isEmpty(labelStr)) {
											LabelWordRelation labelWordRelation = new LabelWordRelation();
											labelWordRelation.setLabelId(labelInfo.getId());
											labelWordRelation.setWordId(wOrdinary.getId());
											labelWordRelationRepository.save(labelWordRelation);
										}
									} catch (JpaSystemException e) {
									}
								}
							} else {
								if (!StringUtil.isEmpty(labelStr)) {
									LabelWordRelation labelWordRelation = new LabelWordRelation();
									labelWordRelation.setLabelId(labelInfo.getId());
									labelWordRelation.setWordId(wordOrdinaryExist.getId());
									labelWordRelationRepository.save(labelWordRelation);
								}
							}
						}
						if (infos.length == 2) {
							if (null != PosConfig.natureNameMap.get(infos[1])) {
								if (infos[0].contains("|")) {
									int index = infos[0].indexOf("|");
									wordName = infos[0].substring(0, index);
									labelStr = infos[0].substring(index + 1);
								} else {
									wordName = infos[0];
									labelStr = "";
								}
								WordOrdinary wordOrdinaryExist = wordOrdinaryRepository.findByNameAndDicId(wordName,
										addedDict.getId());
								if (!StringUtil.isEmpty(labelStr)) {
									labelInfo = labelRepository.findOneByName(labelStr);
									if (null == labelInfo) {
										labelInfo = new Label();
										labelInfo.setName(labelStr);
										labelInfo.setStatus(1);
										labelInfo.setCreateTime(new Date());
										// label.setUserId(userId);
										labelRepository.save(labelInfo);
									} else {
										labelInfo.setStatus(1);
										labelInfo.setUpdateTime(new Date());
										labelRepository.save(labelInfo);
									}
								}
								if (null == wordOrdinaryExist) {
									WordOrdinary wordOrdinary = new WordOrdinary();
									wordOrdinary.setDictionaryId(dicId);
									wordOrdinary.setWord(wordName);
									wordOrdinary.setNature(infos[1]);
									StringBuilder stringBuilder = new StringBuilder();
									stringBuilder.append(dicId).append(wordName).append(infos[1]).append(0);
									valueS = stringBuilder.toString();
									if (!existList.contains(valueS)) {
										existList.add(valueS);
										try {
											ordinaryWordSucceedAllNum += 1;
											wordOrdinaryRepository.save(wordOrdinary);
											WordOrdinary wOrdinary = wordOrdinaryRepository
													.findOne(wordOrdinary.getId());
											if (!StringUtil.isEmpty(labelStr)) {
												LabelWordRelation labelWordRelation = new LabelWordRelation();
												labelWordRelation.setLabelId(labelInfo.getId());
												labelWordRelation.setWordId(wOrdinary.getId());
												labelWordRelationRepository.save(labelWordRelation);
											}
										} catch (JpaSystemException e) {
										}
									}
								} else {
									if (!StringUtil.isEmpty(labelStr)) {
										LabelWordRelation labelWordRelation = new LabelWordRelation();
										labelWordRelation.setLabelId(labelInfo.getId());
										labelWordRelation.setWordId(wordOrdinaryExist.getId());
										labelWordRelationRepository.save(labelWordRelation);
									}
								}
								continue;
							}
							try {
								Integer.parseInt(infos[1]);
							} catch (Exception e) {
								continue;
							}
							if (infos[0].contains("|")) {
								String[] wordArr = infos[0].split("|");
								int index = infos[0].indexOf("|");
								wordName = infos[0].substring(0, index);
								labelStr = infos[0].substring(index + 1);
							} else {
								wordName = infos[0];
								labelStr = "";
							}
							if (!StringUtil.isEmpty(labelStr)) {
								labelInfo = labelRepository.findOneByName(labelStr);
								if (null == labelInfo) {
									labelInfo = new Label();
									labelInfo.setName(labelStr);
									labelInfo.setStatus(1);
									labelInfo.setCreateTime(new Date());
									// label.setUserId(userId);
									labelRepository.save(labelInfo);
								} else {
									labelInfo.setStatus(1);
									labelInfo.setUpdateTime(new Date());
									labelRepository.save(labelInfo);
								}
							}
							WordOrdinary wordOrdinaryExist = wordOrdinaryRepository.findByNameAndDicId(wordName,
									addedDict.getId());
							if (null == wordOrdinaryExist) {
								WordOrdinary wordOrdinary = new WordOrdinary();
								wordOrdinary.setDictionaryId(dicId);
								wordOrdinary.setWord(wordName);
								wordOrdinary.setFrequency(Integer.parseInt(infos[1]));
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(dicId).append(wordName).append(Integer.parseInt(infos[1]));
								valueS = stringBuilder.toString();
								if (!existList.contains(valueS)) {
									existList.add(valueS);
									try {
										ordinaryWordSucceedAllNum += 1;
										wordOrdinaryRepository.save(wordOrdinary);
										WordOrdinary wOrdinary = wordOrdinaryRepository.findOne(wordOrdinary.getId());
										if (!StringUtil.isEmpty(labelStr)) {
											LabelWordRelation labelWordRelation = new LabelWordRelation();
											labelWordRelation.setLabelId(labelInfo.getId());
											labelWordRelation.setWordId(wOrdinary.getId());
											labelWordRelationRepository.save(labelWordRelation);
										}
									} catch (JpaSystemException e) {
									}

								}
							} else {
								if (!StringUtil.isEmpty(labelStr)) {
									LabelWordRelation labelWordRelation = new LabelWordRelation();
									labelWordRelation.setLabelId(labelInfo.getId());
									labelWordRelation.setWordId(wordOrdinaryExist.getId());
									labelWordRelationRepository.save(labelWordRelation);
								}
							}
						} else {
							continue;
						}
					}
				} else {
					throw new AdminException("词库类型传输错误");
				}
			} catch (IOException e) {
				LogManager.Exception("", e);
			}
		}
		// 词典标签关系表添加数据
		String[] functionIds = null;
		if (!StringUtil.isEmpty(functionId)) {
			functionIds = functionId.split(",");
			List<DicFuncRelation> dicFuncRelations = new ArrayList<>();
			for (String functionid : functionIds) {
				DicFuncRelation dicFuncRelation = new DicFuncRelation();
				dicFuncRelation.setDicId(dicId);
				dicFuncRelation.setFunctionId(functionid);
				dicFuncRelation.setStatus(0);
				dicFuncRelations.add(dicFuncRelation);
			}
			if (!dicFuncRelations.isEmpty()) {
				dicFuncRelationRepository.save(dicFuncRelations);
				addedDict.setStatus(1);
				dictRepository.save(addedDict);
			}
		}
		List<Integer> numList = new ArrayList<>();
		numList.add(wordAllNum);
		numList.add(ordinaryWordSucceedAllNum);

		return numList;
	}

	@Override
	public boolean labelDic(String dicId, String labelIds) {
		List<LabelWordRelation> labelWordRelations = labelWordRelationRepository.findAllByDicId(dicId);
		List<String> labelIdsOld = new ArrayList<>();
		if (labelWordRelations.size() > 0) {
			for (LabelWordRelation labelWordRelation : labelWordRelations) {
				labelWordRelationRepository.delete(labelWordRelation);
				labelIdsOld.add(labelWordRelation.getLabelId());
			}
			// 标签是否在使用
			for (String labelIdOld : labelIdsOld) {
				List<LabelWordRelation> labelWordRelationsExist = labelWordRelationRepository
						.findAllByLabelId(labelIdOld);
				if (0 == labelWordRelationsExist.size()) {
					Label label = labelRepository.findOne(labelIdOld);
					label.setStatus(0);
					labelRepository.save(label);
				}
			}
		}
		if (!StringUtil.isEmpty(labelIds)) {
			String[] labelArr = labelIds.split(",");
			for (String labelId : labelArr) {
				Label labelN = labelRepository.findOne(labelId);
				labelN.setStatus(1);
				labelRepository.save(labelN);
				List<WordOrdinary> wordOrdinaries = wordOrdinaryRepository.findAllByDictionaryId(dicId);
				for (WordOrdinary wordOrdinary : wordOrdinaries) {
					LabelWordRelation labelWordRelationExist = labelWordRelationRepository
							.findOneByLabelIdAndWordId(labelId, wordOrdinary.getId());
					if (null != labelWordRelationExist) {
						continue;
					}
					LabelWordRelation labelWordRelation = new LabelWordRelation();
					labelWordRelation.setDicId(dicId);
					labelWordRelation.setLabelId(labelId);
					labelWordRelation.setWordId(wordOrdinary.getId());
					labelWordRelationRepository.save(labelWordRelation);
				}
			}
		}

		return true;
	}

	@Override
	public List<Map<String, String>> labelsInfo(String dicId) {
		List<LabelWordRelation> labelWordRelations = labelWordRelationRepository.findAllByDicId(dicId);
		List<Map<String, String>> labels = new ArrayList<>();
		Map<String, String> labelMap = new HashMap<>();
		if (labelWordRelations.size() > 0) {
			for (LabelWordRelation labelWordRelation : labelWordRelations) {
				labelMap = new HashMap<>();
				String labelId = labelRepository.findOne(labelWordRelation.getLabelId()).getId();
				String labelName = labelRepository.findOne(labelWordRelation.getLabelId()).getName();
				labelMap.put("labelId", labelId);
				labelMap.put("labelName", labelName);
				if (!labels.contains(labelMap)) {
					labels.add(labelMap);
				}
			}
		}

		return labels;
	}

	@Override
	public boolean updateDictionaryName(String dictId, String newDicName) {
		Dictionary dictionaryInfo = dictionaryRepository.findOne(dictId);
		Dictionary dictionaryExist = dictionaryRepository.findByName(newDicName);
		if (!newDicName.equals(dictionaryInfo.getName()) && null != dictionaryExist) {
			throw new AdminException("修改失败，该名称已经存在");
		}
		dictionaryInfo.setName(newDicName);
		dictionaryRepository.save(dictionaryInfo);

		return true;
	}

}
