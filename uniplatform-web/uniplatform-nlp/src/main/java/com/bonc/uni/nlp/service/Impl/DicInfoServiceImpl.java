package com.bonc.uni.nlp.service.Impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.config.PosConfig;
import com.bonc.uni.nlp.dao.DicFuncRelationRepository;
import com.bonc.uni.nlp.dao.DicTypeRepository;
import com.bonc.uni.nlp.dao.DictionaryRepository;
import com.bonc.uni.nlp.dao.FieldTypeRepository;
import com.bonc.uni.nlp.dao.WordMapRepository;
import com.bonc.uni.nlp.dao.WordOrdinaryRepository;
import com.bonc.uni.nlp.dao.WordSentimentRepository;
import com.bonc.uni.nlp.dao.label.LabelRepository;
import com.bonc.uni.nlp.dao.label.LabelWordRelationRepository;
import com.bonc.uni.nlp.entity.dic.DicFuncRelation;
import com.bonc.uni.nlp.entity.dic.DicType;
import com.bonc.uni.nlp.entity.dic.Dictionary;
import com.bonc.uni.nlp.entity.dic.FieldType;
import com.bonc.uni.nlp.entity.dic.WordMap;
import com.bonc.uni.nlp.entity.dic.WordOrdinary;
import com.bonc.uni.nlp.entity.dic.WordSentiment;
import com.bonc.uni.nlp.entity.label.Label;
import com.bonc.uni.nlp.entity.label.LabelWordRelation;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.IDicInfoService;
import com.bonc.uni.nlp.utils.Encoding;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年10月26日 下午3:14:28
 */
@Service
public class DicInfoServiceImpl implements IDicInfoService {

	@Autowired
	WordMapRepository wordMapRepository;
	@Autowired
	WordOrdinaryRepository wordOrdinaryRepository;
	@Autowired
	WordSentimentRepository wordSentimentRepository;
	@Autowired
	DictionaryRepository dictionaryRepository;
	@Autowired
	DicFuncRelationRepository dicFuncRelationRepository;
	@Autowired
	DicTypeRepository dicTypeRepository;
	@Autowired
	FieldTypeRepository fieldTypeRepository;
	@Autowired
	LabelRepository labelRepository;
	@Autowired
	LabelWordRelationRepository labelWordRelationRepository;

	Sort sortMap = new Sort(Sort.Direction.DESC, "createTime");
	Sort sortOr = new Sort(Sort.Direction.DESC, "createTime");
	private Date creatTime;

	@Override
	public Dictionary getDicFormat(String dicId) {
		Dictionary dicFormat = dictionaryRepository.findOneById(dicId);
		return dicFormat;
	}

	@Override
	public List<WordMap> getMapDicInfo(String dicId, String searchWord, int pageIndex, int pageSize) {
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sortMap);
		List<WordMap> MapDicInfo = new ArrayList<>();
		if (StringUtil.isEmpty(searchWord)) {
			MapDicInfo = wordMapRepository.findAllByDictionaryId(dicId, pageable);
		} else {
			searchWord = "%" + searchWord + "%";
			MapDicInfo = wordMapRepository.findAllByNameLike(dicId, searchWord, pageable);
		}
		return MapDicInfo;
	}

	@Override
	public List<Object> getOrdinaryDicInfo(String dicId, String searchWord, int pageIndex, int pageSize) {
		Dictionary dictionary = dictionaryRepository.findOne(dicId);
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sortOr);
		List<WordOrdinary> OrdDicInfo = new ArrayList<>();
		String fieldTypeId = dictionary.getFieldTypeId();
		List<Object> wordList = new ArrayList<>();
		Map<String, Object> wordMap = new HashMap<>();
		if (StringUtil.isEmpty(searchWord)) {
			// 领域词库
			if (!StringUtil.isEmpty(fieldTypeId)) {
				OrdDicInfo = wordOrdinaryRepository.findAllByDictionaryId(dicId, pageable);
				for (WordOrdinary wordOrdinary : OrdDicInfo) {
					String labelName = null;
					wordMap = new HashMap<>();
					List<LabelWordRelation> labelWordRelations = labelWordRelationRepository
							.findAllByWordId(wordOrdinary.getId());
					if (labelWordRelations.size() > 0) {
						StringBuilder wordNameS = new StringBuilder();
						for (LabelWordRelation labelWordRelation : labelWordRelations) {
							Label label = labelRepository.findOne(labelWordRelation.getLabelId());
							wordNameS.append(label.getName()).append("|");
						}
						labelName = wordNameS.toString().substring(0, wordNameS.length() - 1);
					}
					wordMap.put("word", wordOrdinary.getWord());
					wordMap.put("label", labelName);
					wordMap.put("id", wordOrdinary.getId());
					wordMap.put("nature", wordOrdinary.getNature());
					wordMap.put("frequency", wordOrdinary.getFrequency());
					wordMap.put("wordStatus", wordOrdinary.getWordStatus());
					if (!wordList.contains(wordMap)) {
						wordList.add(wordMap);
					}
				}
			} else {
				OrdDicInfo = wordOrdinaryRepository.findAllByDictionaryId(dicId, pageable);
				wordList.addAll(OrdDicInfo);
			}
		} else {
			if (!StringUtil.isEmpty(fieldTypeId)) {
				// 领域词库，人名中包含关键词
				OrdDicInfo = wordOrdinaryRepository.findAllByDictionaryId(dicId, pageable);
				String labelName = null;
				List<String> wordIds = new ArrayList<>();
				for (WordOrdinary wordOrdinary : OrdDicInfo) {
					wordMap = new HashMap<>();
					wordIds.add(wordOrdinary.getId());
					List<LabelWordRelation> labelWordRelations = labelWordRelationRepository
							.findAllByWordId(wordOrdinary.getId());
					if (labelWordRelations.size() > 0) {
						for (LabelWordRelation labelWordRelation : labelWordRelations) {
							Label label = labelRepository.findOne(labelWordRelation.getLabelId());
							StringBuilder wordNameS = new StringBuilder();
							wordNameS.append(label.getName()).append("|");
							labelName = wordNameS.toString().substring(0, wordNameS.length() - 1);
						}
					}
					// 标签中含有关键字
					if (labelName.contains(searchWord) || wordOrdinary.getWord().contains(searchWord)) {
						wordMap.put("word", wordOrdinary.getWord());
						wordMap.put("label", labelName);
						wordMap.put("id", wordOrdinary.getId());
						wordMap.put("nature", wordOrdinary.getNature());
						wordMap.put("frequency", wordOrdinary.getFrequency());
						wordMap.put("wordStatus", wordOrdinary.getWordStatus());
						if (!wordList.contains(wordMap)) {
							wordList.add(wordMap);
						}
					}
				}
			} else {
				searchWord = "%" + searchWord + "%";
				OrdDicInfo = wordOrdinaryRepository.findAllByNameLike(dicId, searchWord, pageable);
				wordList.addAll(OrdDicInfo);
			}
		}
		return wordList;
	}

	@Override
	public List<WordSentiment> getSentimentDicInfo(String dicId, String searchWord, int pageIndex, int pageSize) {
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sortOr);
		List<WordSentiment> OrdDicInfo = new ArrayList<>();
		if (StringUtil.isEmpty(searchWord)) {
			OrdDicInfo = wordSentimentRepository.findAllByDictionaryId(dicId, pageable);
		} else {
			searchWord = "%" + searchWord + "%";
			OrdDicInfo = wordSentimentRepository.findAllByNameLike(dicId, searchWord, pageable);
		}
		return OrdDicInfo;
	}

	@Override
	public boolean addMapWord(String dicId, String wordKey, String wordValue) {
		// TODO 添加到redis
		Dictionary dictionary = dictionaryRepository.findOne(dicId);

		if (dictionary == null) {
			return false;
		}
		// 如果已经存在则不添加
		List<WordMap> wordExist = wordMapRepository.findAllByDictionaryIdAndWordKey(dicId, wordKey);

		if (wordExist != null && !wordExist.isEmpty()) {
			return false;
		}

		WordMap mapWord = new WordMap();
		mapWord.setWordKey(wordKey);
		mapWord.setWordValue(wordValue);
		Date creatTime = new Date();
		mapWord.setCreateTime(creatTime);
		mapWord.setDictionaryId(dicId);
		mapWord.setWordStatus(0);
		WordMap save = wordMapRepository.save(mapWord);

		// IRedisHandler redisHandler =
		// RedisManager.getInstance().getStandaloneHandler();
		// String key = DictionaryConstant.REDIS_WORD_MAP_PREFIX +
		// dictionary.getName() + "_" + save.getWordKey();
		// Map<String, String> wordMap = new HashMap<>();
		// wordMap.put(DictionaryConstant.REDIS_WORD_MAP_KEY,
		// StringUtil.avoidNull(save.getWordKey()));
		// wordMap.put(DictionaryConstant.REDIS_WORD_MAP_VALUE,
		// StringUtil.avoidNull(save.getWordValue()));
		//
		// redisHandler.hmset(key, wordMap);
		return true;
	}

	@Override
	public boolean addOrdWord(String dicId, String word, String wordLabel, String nature, int frequency) {
		Dictionary dictionary = dictionaryRepository.findOne(dicId);
		if (null == dictionary) {
			throw new AdminException("添加失败，该词库不存在");
		}
		// TODO 添加到redis
		WordOrdinary wordExists = wordOrdinaryRepository.findAllByDictionaryIdAndWord(dicId, word);
		if (null != wordExists) {
			throw new AdminException("添加失败，该词已存在");
		}
		// 单独考虑领域词库的人的情况
		String fieldTypeId = dictionary.getFieldTypeId();
		String fieldTypeName = null;
		if (!StringUtil.isEmpty(fieldTypeId)) {
			FieldType fieldType = fieldTypeRepository.findOne(fieldTypeId);
			fieldTypeName = fieldType.getName();
		}
		WordOrdinary wordOrdinary = new WordOrdinary();
		wordOrdinary.setDictionaryId(dicId);
		wordOrdinary.setWord(word);
		wordOrdinary.setNature(nature);
		wordOrdinary.setFrequency(frequency);
		WordOrdinary wOrdinary = wordOrdinaryRepository.save(wordOrdinary);
		if ("人".equals(fieldTypeName)) {
			if (!StringUtil.isEmpty(wordLabel)) {
				if (wordLabel.contains("|")) {
					String[] labelsArr = wordLabel.split("\\|");
					for (String label : labelsArr) {
						Label labelInfo = labelRepository.findOneByName(label);
						if (null == labelInfo) {
							labelInfo = new Label();
							labelInfo.setName(label);
							labelInfo.setStatus(1);
							labelInfo.setCreateTime(new Date());
							// label.setUserId(userId);
							labelInfo = labelRepository.save(labelInfo);
						} else {
							labelInfo.setStatus(1);
							labelInfo.setUpdateTime(new Date());
							labelRepository.save(labelInfo);
						}
						LabelWordRelation labelWordRelation = new LabelWordRelation();
						labelWordRelation.setLabelId(labelInfo.getId());
						labelWordRelation.setWordId(wOrdinary.getId());
						labelWordRelationRepository.save(labelWordRelation);
					}
				} else {
					Label labelInfo = labelRepository.findOneByName(wordLabel);
					if (null == labelInfo) {
						labelInfo = new Label();
						labelInfo.setName(wordLabel);
						labelInfo.setStatus(1);
						labelInfo.setCreateTime(new Date());
						// label.setUserId(userId);
						labelInfo = labelRepository.save(labelInfo);
					} else {
						labelInfo.setStatus(1);
						labelInfo.setUpdateTime(new Date());
						labelRepository.save(labelInfo);
					}
					LabelWordRelation labelWordRelation = new LabelWordRelation();
					labelWordRelation.setLabelId(labelInfo.getId());
					labelWordRelation.setWordId(wOrdinary.getId());
					labelWordRelationRepository.save(labelWordRelation);
				}
			}
		}

		// IRedisHandler redisHandler =
		// RedisManager.getInstance().getStandaloneHandler();
		//
		// String key = DictionaryConstant.REDIS_WORD_PREFIX +
		// dictionary.getName() + "_" + save.getWord();
		// Map<String, String> wordMap = new HashMap<>();
		// wordMap.put(DictionaryConstant.REDIS_WORD_NAME,
		// StringUtil.avoidNull(save.getWord()));
		// wordMap.put(DictionaryConstant.REDIS_WORD_NATURE,
		// StringUtil.avoidNull(save.getNature()));
		// wordMap.put(DictionaryConstant.REDIS_WORD_FREQUENCY,
		// String.valueO1f(save.getFrequency()));
		//
		// redisHandler.hmset(key, wordMap);
		return true;
	}

	@Override
	public boolean addSentimentWord(String dicId, String word, String nature, Double grade) {

		Dictionary dictionary = dictionaryRepository.findOne(dicId);
		// TODO 添加到redis
		List<WordSentiment> wordExists = wordSentimentRepository.findAllByDictionaryIdAndWord(dicId, word);

		if (null != wordExists && !wordExists.isEmpty()) {
			return false;
		}
		WordSentiment sordSentiment = new WordSentiment();
		sordSentiment.setDictionaryId(dicId);
		sordSentiment.setWord(word);
		sordSentiment.setNature(nature);
		Date creatTime = new Date();
		sordSentiment.setCreateTime(creatTime);
		sordSentiment.setWordStatus(0);
		sordSentiment.setGrade(grade);
		WordSentiment save = wordSentimentRepository.save(sordSentiment);

		return true;

	}

	@Override
	public boolean deleteWord(String dicId, List<String> ids) {
		Dictionary dictionary = dictionaryRepository.findOneById(dicId);
		if (null == dictionary)
			return false;
		// IRedisHandler redisHandler =
		// RedisManager.getInstance().getStandaloneHandler();
		// String dictName = dictionary.getName();
		List<WordMap> existedDicMap = wordMapRepository.findAllByDictionaryId(dicId);
		List<WordOrdinary> existedDicOrd = wordOrdinaryRepository.findAllByDictionaryId(dicId);
		List<WordSentiment> existedDicSentiment = wordSentimentRepository.findAllByDictionaryId(dicId);
		if ("map".equals(dictionary.getFormat())) {
			if (null == existedDicMap || existedDicMap.isEmpty()) {
				return false;
			}
			// 如果词库被功能引用，则不允许删除该词
			List<DicFuncRelation> existStatu = dicFuncRelationRepository.findAllByDicIdAndStatus(dicId, 1);
			if (existStatu == null || existStatu.size() == 0) {
				List<String> idsDel = new ArrayList<>();
				for (String wordId : ids) {
					WordMap wordMap = wordMapRepository.findOne(wordId);
					if (1 != wordMap.getWordStatus()) {
						idsDel.add(wordId);
					}
				}
				List<WordMap> mapWords = wordMapRepository.findAll(idsDel);
				// TODO 从redis删除，更改词量数
				// for (WordMap wordMap : mapWords) {
				// String key = DictionaryConstant.REDIS_WORD_MAP_PREFIX +
				// dictName + "_" + wordMap.getWordKey();
				// Map<String, String> words = new HashMap<>();
				// words.put(DictionaryConstant.REDIS_WORD_MAP_KEY,
				// StringUtil.avoidNull(wordMap.getWordKey()));
				// words.put(DictionaryConstant.REDIS_WORD_MAP_VALUE,
				// StringUtil.avoidNull(wordMap.getWordValue()));
				//
				// redisHandler.hmset(key, words);
				// }
				wordMapRepository.delete(mapWords);
			} else {
				return false;
			}
		}
		if ("ordinary".equals(dictionary.getFormat())) {
			if (null == existedDicOrd || existedDicOrd.isEmpty()) {
				return false;
			}
			String fieldTypeId = dictionary.getFieldTypeId();
			// 如果词库被功能引用，则不允许删除该词
			List<DicFuncRelation> existStatu = dicFuncRelationRepository.findAllByDicIdAndStatus(dicId, 1);
			if (existStatu == null || existStatu.size() == 0) {
				List<String> idsDel = new ArrayList<>();
				for (String wordId : ids) {
					WordOrdinary wordOrdinary = wordOrdinaryRepository.findOne(wordId);
					if (1 != wordOrdinary.getWordStatus()) {
						idsDel.add(wordId);
					}
				}
				List<WordOrdinary> ordinaryWords = wordOrdinaryRepository.findAll(idsDel);
				// for (WordOrdinary ordinaryWord : ordinaryWords) {
				// String key = DictionaryConstant.REDIS_WORD_PREFIX + dictName
				// + "_" + ordinaryWord.getWord();
				// Map<String, String> words = new HashMap<>();
				// words.put(DictionaryConstant.REDIS_WORD_NAME,
				// StringUtil.avoidNull(ordinaryWord.getWord()));
				// words.put(DictionaryConstant.REDIS_WORD_NATURE,
				// StringUtil.avoidNull(ordinaryWord.getNature()));
				// words.put(DictionaryConstant.REDIS_WORD_FREQUENCY,
				// String.valueOf(ordinaryWord.getFrequency()));
				// redisHandler.hmset(key, words);
				// }
				wordOrdinaryRepository.delete(ordinaryWords);
				if (!StringUtil.isEmpty(fieldTypeId)) {
					for (WordOrdinary wordOrdinary : ordinaryWords) {
						List<LabelWordRelation> labelWordRelations = labelWordRelationRepository
								.findAllByWordId(wordOrdinary.getId());
						for (LabelWordRelation labelWordRelation : labelWordRelations) {
							if (null != labelWordRelation) {
								labelWordRelationRepository.delete(labelWordRelation);
								List<LabelWordRelation> labelWordRelationExist = labelWordRelationRepository
										.findAllByLabelId(labelWordRelation.getLabelId());
								if (0 == labelWordRelationExist.size()) {
									Label label = labelRepository.findOne(labelWordRelation.getLabelId());
									label.setStatus(0);
									labelRepository.save(label);
								}
							}
						}
					}
				}
			} else {
				return false;
			}
		}
		if ("sentiment".equals(dictionary.getFormat())) {
			if (null == existedDicSentiment || existedDicSentiment.isEmpty()) {
				return false;
			}
			// 如果词库被功能引用，则不允许删除该词
			List<DicFuncRelation> existStatu = dicFuncRelationRepository.findAllByDicIdAndStatus(dicId, 1);
			if (existStatu == null || existStatu.size() == 0) {
				List<String> idsDel = new ArrayList<>();
				for (String wordId : ids) {
					WordSentiment wordSentiment = wordSentimentRepository.findOne(wordId);
					if (1 != wordSentiment.getWordStatus()) {
						idsDel.add(wordId);
					}
				}
				List<WordSentiment> sentimentWords = wordSentimentRepository.findAll(idsDel);
				wordSentimentRepository.delete(sentimentWords);
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean editMapWord(String dicId, String wordId, String newKey, String newValue) {
		WordMap word = wordMapRepository.findOne(wordId);
		// String wordName = word.getWordKey();
		if (null == word)
			throw new AdminException("修改失败，该词不存在");
		WordMap wordMapExist = wordMapRepository.findOneByWordKeyAndDictionaryId(newKey, dicId);
		if (null != wordMapExist) {
			if (!wordId.equals(wordMapExist.getId())) {
				throw new AdminException("修改失败，该词已存在，请重新输入");
			}
		}
		// IRedisHandler redisHandler =
		// RedisManager.getInstance().getStandaloneHandler();
		//
		// Dictionary dic = dictionaryRepository.findOne(dicId);
		// String dictName = dic.getName();
		// 如果词库被功能引用，则不允许修改该词
		List<DicFuncRelation> existStatu = dicFuncRelationRepository.findAllByDicIdAndStatus(dicId, 1);
		if (existStatu == null || existStatu.size() == 0) {
			word.setWordKey(newKey);
			word.setWordValue(newValue);
			Date creatTime = new Date();
			word.setCreateTime(creatTime);
			wordMapRepository.save(word);
			// String key = DictionaryConstant.REDIS_WORD_MAP_PREFIX + dictName
			// + "_" + wordName;
			// Map<String, String> words = new HashMap<>();
			//
			// words.put(DictionaryConstant.REDIS_WORD_MAP_VALUE,
			// StringUtil.avoidNull(word.getWordValue()));
			//
			// redisHandler.hmset(key, words);
		} else {
			throw new AdminException("修改失败，该词库被功能引用，不能修改");
		}

		return true;
	}

	@Override
	public boolean editOrdinaryWord(String dicId, String wordId, String newWord, String wordLabel, String newNature,
			int newFrequency) {
		WordOrdinary word = wordOrdinaryRepository.findOne(wordId);
		if (null == word)
			throw new AdminException("修改失败，该词不存在");
		Dictionary dictionary = dictionaryRepository.findOne(dicId);
		String fieldTypeId = dictionary.getFieldTypeId();
		String fieldTypeName = null;
		if (!StringUtil.isEmpty(fieldTypeId)) {
			FieldType fieldType = fieldTypeRepository.findOne(fieldTypeId);
			fieldTypeName = fieldType.getName();
		}
		WordOrdinary wOrdinary = wordOrdinaryRepository.findByNameAndDicId(newWord, dicId);
		if (wOrdinary != null) {
			if (!wordId.equals(wOrdinary.getId())) {
				throw new AdminException("修改失败，该词已存在，请重新输入");
			}
		}
		// 如果词库被功能引用，则不允许修改该词
		List<DicFuncRelation> existStatu = dicFuncRelationRepository.findAllByDicIdAndStatus(dicId, 1);
		if (existStatu == null || existStatu.size() == 0) {
			word.setWord(newWord);
			word.setNature(newNature);
			word.setFrequency(newFrequency);
			Date creatTime = new Date();
			word.setCreateTime(creatTime);
			wordOrdinaryRepository.save(word);
		} else {
			throw new AdminException("修改失败，该词已被功能引用");
		}
		if ("人".equals(fieldTypeName)) {
			List<LabelWordRelation> labelWordRelations = labelWordRelationRepository.findAllByWordId(wordId);
			// 旧词中没有标签
			if (0 == labelWordRelations.size()) {
				// 新词中有标签，保存新词和标签的关系
				if (!StringUtil.isEmpty(wordLabel)) {
					if (wordLabel.contains("|")) {
						String[] labelsArr = wordLabel.split("\\|");
						for (String labelName : labelsArr) {
							Label label = labelRepository.findOneByName(labelName);
							if (null == label) {
								label = new Label();
								label.setName(labelName);
								label.setCreateTime(new Date());
							}
							label.setStatus(1);
							Label labelExist = labelRepository.save(label);
							LabelWordRelation labelWordRelation = new LabelWordRelation();
							labelWordRelation.setLabelId(labelExist.getId());
							labelWordRelation.setWordId(wordId);
							labelWordRelationRepository.save(labelWordRelation);
						}
					}
				} else {
					Label labelInfo = labelRepository.findOneByName(wordLabel);
					if (null == labelInfo) {
						labelInfo = new Label();
						labelInfo.setName(wordLabel);
						labelInfo.setStatus(1);
						labelInfo.setCreateTime(new Date());
						// label.setUserId(userId);
						labelInfo = labelRepository.save(labelInfo);
					} else {
						labelInfo.setStatus(1);
						labelInfo.setUpdateTime(new Date());
						labelRepository.save(labelInfo);
					}
					LabelWordRelation labelWordRelation = new LabelWordRelation();
					labelWordRelation.setLabelId(labelInfo.getId());
					labelWordRelation.setWordId(wOrdinary.getId());
					labelWordRelationRepository.save(labelWordRelation);
				}
			} // 旧词中有标签
			else {
				if (StringUtil.isEmpty(wordLabel)) {
					// 新词中没有标签，删除旧词和标签的关系
					for (LabelWordRelation labelWordRelation : labelWordRelations) {
						labelWordRelationRepository.delete(labelWordRelation);
						Label label = labelRepository.findOne(labelWordRelation.getLabelId());
						List<LabelWordRelation> labelWordRelationsExist = labelWordRelationRepository
								.findAllByLabelId(label.getId());
						if (0 == labelWordRelationsExist.size()) {
							label.setStatus(0);
							labelRepository.save(label);
						}
					}
				} else {
					// 新词中有标签
					for (LabelWordRelation labelWordRelation : labelWordRelations) {
						Label label = labelRepository.findOne(labelWordRelation.getLabelId());
						labelWordRelationRepository.delete(labelWordRelation);
						List<LabelWordRelation> labelWordRelationsExist = labelWordRelationRepository
								.findAllByLabelId(label.getId());
						if (0 == labelWordRelationsExist.size()) {
							label.setStatus(0);
							labelRepository.save(label);
						}
					}
					if (wordLabel.contains("|")) {
						String[] labelArr = wordLabel.split("\\|");
						for (String labelName : labelArr) {
							Label label = labelRepository.findOneByName(labelName);
							if (null == label) {
								label = new Label();
								label.setName(labelName);
								label.setCreateTime(new Date());
							}
							label.setStatus(1);
							Label labelExist = labelRepository.save(label);
							LabelWordRelation labelWordRelation = new LabelWordRelation();
							labelWordRelation.setLabelId(labelExist.getId());
							labelWordRelation.setWordId(wordId);
							labelWordRelationRepository.save(labelWordRelation);
						}
					} else {
						Label label = labelRepository.findOneByName(wordLabel);
						if (null == label) {
							label = new Label();
							label.setName(wordLabel);
							label.setCreateTime(new Date());
						}
						label.setStatus(1);
						Label labelExist = labelRepository.save(label);
						LabelWordRelation labelWordRelation = new LabelWordRelation();
						labelWordRelation.setLabelId(labelExist.getId());
						labelWordRelation.setWordId(wordId);
						labelWordRelationRepository.save(labelWordRelation);
					}

				}
			}
		}

		return true;
	}

	@Override
	public boolean editSentimentWord(String dicId, String wordId, String newWord, String newNature, Double grade) {
		WordSentiment word = wordSentimentRepository.findOne(wordId);
		if (null == word)
			throw new AdminException("修改失败，该词不存在");
		WordSentiment wordExist = wordSentimentRepository.findOneByWord(newWord);
		if (null != wordExist) {
			if (!wordId.equals(wordExist.getId())) {
				throw new AdminException("修改失败，该词已存在");
			}
		}
		// 如果词库被功能引用，则不允许修改该词
		List<DicFuncRelation> existStatu = dicFuncRelationRepository.findAllByDicIdAndStatus(dicId, 1);
		if (existStatu == null || existStatu.size() == 0) {
			word.setWord(newWord);
			word.setNature(newNature);
			word.setGrade(grade);
			Date creatTime = new Date();
			word.setCreateTime(creatTime);
			wordSentimentRepository.save(word);
		} else {
			throw new AdminException("修改失败，该词已被功能引用");
		}

		return true;
	}

	@Override
	public int getMapWordNumbers(String dicId, String searchWord) {
		searchWord = "%" + searchWord + "%";
		int wordMapNumbers = wordMapRepository.countByDictionaryId(dicId, searchWord);
		return wordMapNumbers;
	}

	@Override
	public int getOrdinaryWordNumbers(String dicId, String searchWord) {
		searchWord = "%" + searchWord + "%";
		int wordOrdinaryNumbers = wordOrdinaryRepository.countByDictionaryId(dicId, searchWord);
		return wordOrdinaryNumbers;
	}
	
	@Override
	public int getSentimentWordNumbers(String dicId, String searchWord) {
		searchWord = "%" + searchWord + "%";
		int wordOrdinaryNumbers = wordSentimentRepository.countByDictionaryId(dicId, searchWord);
		return wordOrdinaryNumbers;
	}

	@Override
	public List<Integer> uploadWords2Dict(String dictId, MultipartFile[] files) {
		Dictionary dic = dictionaryRepository.findOne(dictId);
		if (null == dic) {
			throw new AdminException("上传失败，该词库不存在");
		}
		List<WordMap> mapWords = new ArrayList<>();
		List<WordOrdinary> ordinaryWords = new ArrayList<>();
		List<WordSentiment> sentimentWords = new ArrayList<>();
		int wordSuccessNum = 0;
		int wordNumber = 0;
		List<String> existList = new ArrayList<>();
		String valueS = null;
		for (MultipartFile file : files) {
			if (null == file) {
				continue;
			}
			try (InputStream inStream = file.getInputStream();
					InputStreamReader inReader = new InputStreamReader(inStream);
					BufferedReader reader = new BufferedReader(inReader)) {
				if (!"UTF-8".equals(Encoding.tryEncoding(file))) {
					throw new AdminException("请上传utf-8格式的文件");
				}
				String dictMsg = null;
				if ("map".equals(dic.getFormat())) {
					Map<String, String> wordMaps = new HashMap<>();
					List<WordMap> wordMapList = wordMapRepository.findAllByDictionaryId(dictId);
					if (null != wordMapList || wordMapList.size() != 0) {
						for (WordMap wordMap : wordMapList) {
							wordMaps.put(wordMap.getWordKey(), wordMap.getWordValue());
						}
					}
					while (null != (dictMsg = reader.readLine())) {
						if (dictMsg.isEmpty()) {
							continue;
						}
						wordNumber += 1;
						WordMap mapWord = new WordMap();
						if (dictMsg.startsWith("\uFEFF"))
							dictMsg = dictMsg.replace("\uFEFF", "");
						try {
							String[] wordProps = dictMsg.split("=");
							if (2 != wordProps.length)
								continue;
							if (wordMaps != null && wordMaps.size() != 0) {
								Set<String> keySet = wordMaps.keySet();
								if (!keySet.contains(wordProps[0].trim())) {
									mapWord.setDictionaryId(dictId);
									String wordKey = wordProps[0].trim();
									mapWord.setWordKey(wordKey.trim());
									String wordValue = wordProps[1].replaceAll("(，|,)", ",").trim();
									mapWord.setWordValue(wordValue.trim());
									WordMap wordMapExist = wordMapRepository.findByDictionaryIdAnd(dictId, wordKey,
											wordValue);
									if (null != wordMapExist) {
										continue;
									}
									StringBuilder stringBuilder = new StringBuilder();
									stringBuilder.append(dictId).append(wordProps[0].trim()).append(wordValue);
									valueS = stringBuilder.toString();
									if (!existList.contains(valueS)) {
										existList.add(valueS);
										wordSuccessNum += 1;
										mapWords.add(mapWord);
									}
								}
							} else {
								mapWord.setDictionaryId(dictId);
								String wordKey = wordProps[0].trim();
								mapWord.setWordKey(wordKey);
								String wordValue = wordProps[1].trim();
								mapWord.setWordValue(wordValue);
								WordMap wordMapExist = wordMapRepository.findByDictionaryIdAnd(dictId, wordKey,
										wordValue);
								if (null != wordMapExist) {
									continue;
								}
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(dictId).append(wordProps[0].trim()).append(wordProps[1].trim());
								valueS = stringBuilder.toString();
								if (!existList.contains(valueS)) {
									existList.add(valueS);
									wordSuccessNum += 1;
									mapWords.add(mapWord);
								}
							}
							// String key =
							// DictionaryConstant.REDIS_WORD_MAP_PREFIX
							// + dictName + "_" + wordProps[0].trim();
							// Map<String, String> words = new HashMap<>();
							// words.put(DictionaryConstant.REDIS_WORD_MAP_KEY,
							// StringUtil.avoidNull(wordProps[0].trim()));
							// words.put(DictionaryConstant.REDIS_WORD_MAP_VALUE,
							// StringUtil.avoidNull(wordProps[1].trim()));
							//
							// redisHandler.hmset(key, words);

						} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
							LogManager.Exception(e);
							continue;
						}
					}
					if (mapWords != null && mapWords.size() != 0) {
						wordMapRepository.save(mapWords);
					}
				}
				if ("ordinary".equals(dic.getFormat())) {
					List<WordOrdinary> wordOrdinarys = wordOrdinaryRepository.findAllByDictionaryId(dictId);
					List<String> wordOrdinaryStrs = new ArrayList<>();
					if (wordOrdinarys != null && wordOrdinarys.size() != 0) {
						for (WordOrdinary wordOrdinary : wordOrdinarys) {
							wordOrdinaryStrs.add(wordOrdinary.getWord());
						}
					}
					String fieldTypeId = dic.getFieldTypeId();
					while (null != (dictMsg = reader.readLine())) {
						if (StringUtil.isEmpty(fieldTypeId)) {
							if (dictMsg.isEmpty()) {
								continue;
							}
							wordNumber += 1;
							try {
								String[] wordProps = dictMsg.split(",|，");
								if (wordProps.length == 3) {
									if ("=".equals(wordProps[1]) || wordProps[1].contains("=")) {
										continue;
									}
									if (null == PosConfig.natureNameMap.get(wordProps[1])) {
										continue;
									}
									try {
										Integer.parseInt(wordProps[2]);
									} catch (Exception e) {
										continue;
									}
									if (wordOrdinaryStrs != null && wordOrdinaryStrs.size() != 0) {
										if (!wordOrdinaryStrs.contains(wordProps[0])) {
											WordOrdinary wordOrdinary = new WordOrdinary();
											wordOrdinary.setDictionaryId(dictId);
											wordOrdinary.setWord(wordProps[0].trim());
											wordOrdinary.setNature(wordProps[1].trim());
											wordOrdinary.setFrequency(Integer.parseInt(wordProps[2]));
											WordOrdinary wordOrdinaryExist = wordOrdinaryRepository
													.findByDictionaryIdAnd(dictId, wordProps[0].trim(),
															wordProps[1].trim(), Integer.parseInt(wordProps[2]));
											if (null != wordOrdinaryExist) {
												continue;
											}
											StringBuilder stringBuilder = new StringBuilder();
											stringBuilder.append(dictId).append(wordProps[0].trim())
													.append(wordProps[1].trim()).append(Integer.parseInt(wordProps[2]));
											valueS = stringBuilder.toString();
											if (!existList.contains(valueS)) {
												existList.add(valueS);
												wordSuccessNum += 1;
												ordinaryWords.add(wordOrdinary);
											}
										}
									} else {
										WordOrdinary wordOrdinary = new WordOrdinary();
										wordOrdinary.setDictionaryId(dictId);
										wordOrdinary.setWord(wordProps[0].trim());
										wordOrdinary.setNature(wordProps[1].trim());
										wordOrdinary.setFrequency(Integer.parseInt(wordProps[2]));
										WordOrdinary wordOrdinaryExist = wordOrdinaryRepository.findByDictionaryIdAnd(
												dictId, wordProps[0].trim(), wordProps[1].trim(),
												Integer.parseInt(wordProps[2]));
										if (null != wordOrdinaryExist) {
											continue;
										}
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(dictId).append(wordProps[0].trim())
												.append(wordProps[1].trim()).append(Integer.parseInt(wordProps[2]));
										valueS = stringBuilder.toString();
										if (!existList.contains(valueS)) {
											existList.add(valueS);
											wordSuccessNum += 1;
											ordinaryWords.add(wordOrdinary);
										}
									}
									// String key =
									// DictionaryConstant.REDIS_WORD_PREFIX
									// + dictName + "_" + wordName;
									// Map<String, String> words = new
									// HashMap<>();
									// words.put(DictionaryConstant.REDIS_WORD_NAME,
									// StringUtil.avoidNull(wordName));
									// words.put(DictionaryConstant.REDIS_WORD_NATURE,
									// StringUtil.avoidNull(wordNature));
									// words.put(DictionaryConstant.REDIS_WORD_FREQUENCY,
									// String.valueOf(wordFrequency));
									// redisHandler.hmset(key, words);
								}
								if (wordProps.length == 2) {
									if (wordOrdinaryStrs != null && wordOrdinaryStrs.size() != 0) {
										if (!wordOrdinaryStrs.contains(wordProps[0])) {
											if (null != PosConfig.natureNameMap.get(wordProps[1])) {
												String wordName = wordProps[0];
												WordOrdinary wordOrdinary = new WordOrdinary();
												wordOrdinary.setDictionaryId(dictId);
												wordOrdinary.setWord(wordName.trim());
												wordOrdinary.setNature(wordProps[1].trim());
												WordOrdinary wordOrdinaryExist = wordOrdinaryRepository
														.findByDictionaryIdAnd(dictId, wordName.trim(),
																wordProps[1].trim(), 0);
												if (null != wordOrdinaryExist) {
													continue;
												}
												StringBuilder stringBuilder = new StringBuilder();
												stringBuilder.append(dictId).append(wordName.trim())
														.append(wordProps[1].trim()).append(0);
												valueS = stringBuilder.toString();
												if (!existList.contains(valueS)) {
													existList.add(valueS);
													wordSuccessNum += 1;
													ordinaryWords.add(wordOrdinary);
												}
												continue;
											}
											try {
												Integer.parseInt(wordProps[1]);
											} catch (Exception e) {
												continue;
											}
											WordOrdinary wordOrdinary = new WordOrdinary();
											wordOrdinary.setWord(wordProps[0].trim());
											wordOrdinary.setFrequency(Integer.parseInt(wordProps[1]));
											wordOrdinary.setDictionaryId(dictId);
											WordOrdinary wordOrdinaryExist = wordOrdinaryRepository
													.findByDictionaryIdAnd(dictId, wordProps[0].trim(), "",
															Integer.parseInt(wordProps[1]));
											if (null != wordOrdinaryExist) {
												continue;
											}
											StringBuilder stringBuilder = new StringBuilder();
											stringBuilder.append(wordProps[0].trim())
													.append(Integer.parseInt(wordProps[1])).append(dictId);
											valueS = stringBuilder.toString();
											if (!existList.contains(valueS)) {
												existList.add(valueS);
												wordSuccessNum += 1;
												ordinaryWords.add(wordOrdinary);
											}
										}
									} else {
										if (null != PosConfig.natureNameMap.get(wordProps[1])) {
											String wordName = wordProps[0];
											WordOrdinary wordOrdinary = new WordOrdinary();
											wordOrdinary.setDictionaryId(dictId);
											wordOrdinary.setWord(wordName.trim());
											wordOrdinary.setNature(wordProps[1].trim());
											WordOrdinary wordOrdinaryExist = wordOrdinaryRepository
													.findByDictionaryIdAnd(dictId, wordName.trim(), wordProps[1].trim(),
															0);
											if (null != wordOrdinaryExist) {
												continue;
											}
											StringBuilder stringBuilder = new StringBuilder();
											stringBuilder.append(dictId).append(wordName.trim())
													.append(wordProps[1].trim()).append(0);
											valueS = stringBuilder.toString();
											if (!existList.contains(valueS)) {
												existList.add(valueS);
												wordSuccessNum += 1;
												ordinaryWords.add(wordOrdinary);
											}
											continue;
										}
										try {
											Integer.parseInt(wordProps[1]);
										} catch (Exception e) {
											continue;
										}
										WordOrdinary wordOrdinary = new WordOrdinary();
										wordOrdinary.setWord(wordProps[0].trim());
										wordOrdinary.setFrequency(Integer.parseInt(wordProps[1]));
										wordOrdinary.setDictionaryId(dictId);
										WordOrdinary wordOrdinaryExist = wordOrdinaryRepository.findByDictionaryIdAnd(
												dictId, wordProps[0].trim(), "", Integer.parseInt(wordProps[1]));
										if (null != wordOrdinaryExist) {
											continue;
										}
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(dictId).append(wordProps[0].trim())
												.append(Integer.parseInt(wordProps[1]));
										valueS = stringBuilder.toString();
										if (!existList.contains(valueS)) {
											existList.add(valueS);
											wordSuccessNum += 1;
											ordinaryWords.add(wordOrdinary);
										}
									}
									// String key =
									// DictionaryConstant.REDIS_WORD_PREFIX
									// + dictName + "_" + wordName;
									// Map<String, String> words = new
									// HashMap<>();
									// words.put(DictionaryConstant.REDIS_WORD_NAME,
									// StringUtil.avoidNull(wordName));
									// redisHandler.hmset(key, words);
								}
								if (wordProps.length == 1) {
									if (wordOrdinaryStrs != null && wordOrdinaryStrs.size() != 0) {
										if (!wordOrdinaryStrs.contains(wordProps[0])) {
											WordOrdinary wordOrdinary = new WordOrdinary();
											wordOrdinary.setDictionaryId(dictId);
											wordOrdinary.setWord(wordProps[0].trim());
											WordOrdinary wordOrdinaryExist = wordOrdinaryRepository
													.findByDictionaryIdAnd(dictId, wordProps[0].trim(), "", 0);
											if (null != wordOrdinaryExist) {
												continue;
											}
											StringBuilder stringBuilder = new StringBuilder();
											stringBuilder.append(dictId).append(wordProps[0].trim()).append(0);
											valueS = stringBuilder.toString();
											if (!existList.contains(valueS)) {
												existList.add(valueS);
												wordSuccessNum += 1;
												ordinaryWords.add(wordOrdinary);
											}
										}
									} else {
										WordOrdinary wordOrdinary = new WordOrdinary();
										wordOrdinary.setDictionaryId(dictId);
										wordOrdinary.setWord(wordProps[0].trim());
										WordOrdinary wordOrdinaryExist = wordOrdinaryRepository
												.findByDictionaryIdAnd(dictId, wordProps[0].trim(), "", 0);
										if (null != wordOrdinaryExist) {
											continue;
										}
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(dictId).append(wordProps[0].trim()).append(0);
										valueS = stringBuilder.toString();
										if (!existList.contains(valueS)) {
											existList.add(valueS);
											wordSuccessNum += 1;
											ordinaryWords.add(wordOrdinary);
										}
									}
									// String key =
									// DictionaryConstant.REDIS_WORD_PREFIX
									// + dictName + "_" + wordName;
									// Map<String, String> words = new
									// HashMap<>();
									// words.put(DictionaryConstant.REDIS_WORD_NAME,
									// StringUtil.avoidNull(wordName));
									// redisHandler.hmset(key, words);
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								LogManager.Exception(e);
								continue;
							}
						} else {
							List<WordOrdinary> wordOrdinarysField = wordOrdinaryRepository
									.findAllByDictionaryId(dictId);
							List<String> wordOrdinaryStrsField = new ArrayList<>();
							if (wordOrdinarys != null && wordOrdinarys.size() != 0) {
								for (WordOrdinary wordOrdinary : wordOrdinarysField) {
									List<LabelWordRelation> labelWordRelationEs = labelWordRelationRepository
											.findAllByWordId(wordOrdinary.getId());
									if (labelWordRelationEs.size() > 0) {
										for (LabelWordRelation labelWordRelationE : labelWordRelationEs) {
											Label labelE = labelRepository.findOne(labelWordRelationE.getLabelId());
											StringBuilder stringBuilder = new StringBuilder();
											stringBuilder.append(wordOrdinary.getWord()).append("|")
													.append(labelE.getName());
											wordOrdinaryStrsField.add(stringBuilder.toString());
										}
									} else {
										wordOrdinaryStrsField.add(wordOrdinary.getWord());
									}
								}
							}
								if (StringUtil.isEmpty(dictMsg)) {
									continue;
								}
								wordNumber += 1;
								String wordName = null;
								String labelStr = null;
								Label labelInfo = new Label();
								if (dictMsg.startsWith("\uFEFF"))
									dictMsg = dictMsg.replace("\uFEFF", "");
								String[] infos = dictMsg.split(",|，");
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
											dictId);
									if (!StringUtil.isEmpty(labelStr)) {
										labelInfo = labelRepository.findOneByName(labelStr);
										if (null == labelInfo) {
											labelInfo = new Label();
											labelInfo.setName(labelStr);
											labelInfo.setStatus(0);
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
										wordOrdinary.setWord(wordName.trim());
										wordOrdinary.setNature(infos[1].trim());
										wordOrdinary.setFrequency(Integer.parseInt(infos[2]));
										wordOrdinary.setDictionaryId(dictId);
										WordOrdinary wordOrdinaryExisted = wordOrdinaryRepository.findByDictionaryIdAnd(
												dictId, wordName.trim(), infos[1].trim(), Integer.parseInt(infos[2]));
										if (null != wordOrdinaryExisted) {
											continue;
										}
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(wordName.trim()).append(infos[1].trim())
												.append(Integer.parseInt(infos[2])).append(dictId);
										valueS = stringBuilder.toString();
										if (!existList.contains(valueS)) {
											wordSuccessNum += 1;
											existList.add(valueS);
											WordOrdinary wOrdinary = wordOrdinaryRepository.save(wordOrdinary);
											if (!StringUtil.isEmpty(labelStr)) {
												LabelWordRelation labelWordRelation = new LabelWordRelation();
												labelWordRelation.setLabelId(labelInfo.getId());
												labelWordRelation.setWordId(wOrdinary.getId());
												labelWordRelationRepository.save(labelWordRelation);
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
									if (infos[0].contains("|")) {
										int index = infos[0].indexOf("|");
										wordName = infos[0].substring(0, index);
										labelStr = infos[0].substring(index + 1);
									} else {
										wordName = infos[0];
										labelStr = "";
									}
									WordOrdinary wordOrdinaryExist = wordOrdinaryRepository.findByNameAndDicId(wordName,
											dictId);
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
										wordOrdinary.setCreateTime(new Date());
										wordOrdinary.setWord(wordName.trim());
										wordOrdinary.setWordStatus(0);
										wordOrdinary.setDictionaryId(dictId);
										WordOrdinary wordOrdinaryExisted = wordOrdinaryRepository
												.findByDictionaryIdAnd(dictId, wordName.trim(), "", 0);
										if (null != wordOrdinaryExisted) {
											continue;
										}
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(wordName.trim()).append(0).append(0).append(dictId);
										valueS = stringBuilder.toString();
										if (!existList.contains(valueS)) {
											existList.add(valueS);
											wordSuccessNum += 1;
											WordOrdinary wOrdinary = wordOrdinaryRepository.save(wordOrdinary);
											if (!StringUtil.isEmpty(labelStr)) {
												LabelWordRelation labelWordRelation = new LabelWordRelation();
												labelWordRelation.setLabelId(labelInfo.getId());
												labelWordRelation.setWordId(wOrdinary.getId());
												labelWordRelationRepository.save(labelWordRelation);
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
										WordOrdinary wordOrdinaryExist = wordOrdinaryRepository
												.findByNameAndDicId(wordName.trim(), dictId);
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
											wordOrdinary.setWord(wordName.trim());
											wordOrdinary.setNature(infos[1].trim());
											wordOrdinary.setDictionaryId(dictId);
											WordOrdinary wordOrdinaryExisted = wordOrdinaryRepository
													.findByDictionaryIdAnd(dictId, wordName.trim(), infos[1].trim(), 0);
											if (null != wordOrdinaryExisted) {
												continue;
											}
											StringBuilder stringBuilder = new StringBuilder();
											stringBuilder.append(wordName.trim()).append(infos[1].trim()).append(0)
													.append(dictId);
											valueS = stringBuilder.toString();
											if (!existList.contains(valueS)) {
												existList.add(valueS);
												wordSuccessNum += 1;
												WordOrdinary wOrdinary = wordOrdinaryRepository.save(wordOrdinary);
												if (!StringUtil.isEmpty(labelStr)) {
													LabelWordRelation labelWordRelation = new LabelWordRelation();
													labelWordRelation.setLabelId(labelInfo.getId());
													labelWordRelation.setWordId(wOrdinary.getId());
													labelWordRelationRepository.save(labelWordRelation);
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
											dictId);
									if (null == wordOrdinaryExist) {
										WordOrdinary wordOrdinary = new WordOrdinary();
										wordOrdinary.setWord(wordName.trim());
										wordOrdinary.setFrequency(Integer.parseInt(infos[1]));
										wordOrdinary.setDictionaryId(dictId);
										WordOrdinary wordOrdinaryExisted = wordOrdinaryRepository.findByDictionaryIdAnd(
												dictId, wordName.trim(), "", Integer.parseInt(infos[1]));
										if (null != wordOrdinaryExisted) {
											continue;
										}
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(wordName.trim()).append(Integer.parseInt(infos[1]))
												.append(dictId);
										valueS = stringBuilder.toString();
										if (!existList.contains(valueS)) {
											existList.add(valueS);
											wordSuccessNum += 1;
											WordOrdinary wOrdinary = wordOrdinaryRepository.save(wordOrdinary);
											if (!StringUtil.isEmpty(labelStr)) {
												LabelWordRelation labelWordRelation = new LabelWordRelation();
												labelWordRelation.setLabelId(labelInfo.getId());
												labelWordRelation.setWordId(wOrdinary.getId());
												labelWordRelationRepository.save(labelWordRelation);
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
						if (ordinaryWords != null && ordinaryWords.size() != 0) {
							wordOrdinaryRepository.save(ordinaryWords);
						}
					}
				}
				if ("sentiment".equals(dic.getFormat())) {
					List<WordSentiment> wordSentiments = wordSentimentRepository.findAllByDictionaryId(dictId);
					List<String> wordSentimentStrs = new ArrayList<>();
					if (wordSentiments != null && wordSentiments.size() != 0) {
						for (WordSentiment wordOrdinary : wordSentiments) {
							wordSentimentStrs.add(wordOrdinary.getWord());
						}
					}
					while ((dictMsg = reader.readLine()) != null) {
						if (dictMsg.isEmpty()) {
							continue;
						}
						wordNumber += 1;
						try {
							String[] wordProps = dictMsg.split(",|，");
							if (wordProps.length == 3) {
								if ("=".equals(wordProps[1]) || wordProps[1].contains("=")) {
									continue;
								}
								if (wordSentimentStrs != null && wordSentimentStrs.size() != 0) {
									if (!wordSentimentStrs.contains(wordProps[0])) {
										WordSentiment wordSentiment = new WordSentiment();
										wordSentiment.setDictionaryId(dictId);
										wordSentiment.setWord(wordProps[0].trim());
										wordSentiment.setNature(wordProps[1].trim());
										wordSentiment.setGrade(Double.parseDouble(wordProps[2]));
										WordSentiment wordSentimentExisted = wordSentimentRepository
												.findByDictionaryIdAnd(dictId, wordProps[0].trim(), wordProps[1].trim(),
														Double.parseDouble(wordProps[2]));
										if (null != wordSentimentExisted) {
											continue;
										}
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(dictId).append(wordProps[0].trim())
												.append(wordProps[1].trim()).append(Double.parseDouble(wordProps[2]));
										valueS = stringBuilder.toString();
										if (!existList.contains(valueS)) {
											existList.add(valueS);
											wordSuccessNum += 1;
											sentimentWords.add(wordSentiment);
										}
									}
								} else {
									WordSentiment wordSentiment = new WordSentiment();
									wordSentiment.setDictionaryId(dictId);
									wordSentiment.setWord(wordProps[0].trim());
									wordSentiment.setNature(wordProps[1].trim());
									wordSentiment.setGrade(Double.parseDouble(wordProps[2]));
									WordSentiment wordSentimentExisted = wordSentimentRepository.findByDictionaryIdAnd(
											dictId, wordProps[0].trim(), wordProps[1].trim(),
											Double.parseDouble(wordProps[2]));
									if (null != wordSentimentExisted) {
										continue;
									}
									StringBuilder stringBuilder = new StringBuilder();
									stringBuilder.append(dictId).append(wordProps[0].trim()).append(wordProps[1].trim())
											.append(Double.parseDouble(wordProps[2]));
									valueS = stringBuilder.toString();
									if (!existList.contains(valueS)) {
										existList.add(valueS);
										wordSuccessNum += 1;
										sentimentWords.add(wordSentiment);
									}
								}
							}
							if (wordProps.length == 2) {
								Pattern pattern = Pattern.compile("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+");
								Matcher isNum = pattern.matcher(wordProps[1].trim());
								if (wordSentimentStrs != null && wordSentimentStrs.size() != 0) {
									if (!wordSentimentStrs.contains(wordProps[0])) {
										if (!isNum.matches()) {
											String wordName = wordProps[0];
											WordSentiment wordSentiment = new WordSentiment();
											wordSentiment.setDictionaryId(dictId);
											wordSentiment.setWord(wordName.trim());
											wordSentiment.setNature(wordProps[1].trim());
											WordSentiment wordSentimentExisted = wordSentimentRepository
													.findByDictionaryIdAnd(dictId, wordName.trim(), wordProps[1].trim(),
															0.0);
											if (null != wordSentimentExisted) {
												continue;
											}
											StringBuilder stringBuilder = new StringBuilder();
											stringBuilder.append(dictId).append(wordName.trim())
													.append(wordProps[1].trim()).append(0.0);
											valueS = stringBuilder.toString();
											if (!existList.contains(valueS)) {
												existList.add(valueS);
												wordSuccessNum += 1;
												sentimentWords.add(wordSentiment);
											}
											continue;
										}
										WordSentiment wordSentiment = new WordSentiment();
										wordSentiment.setWord(wordProps[0].trim());
										wordSentiment.setGrade(Double.parseDouble(wordProps[1]));
										wordSentiment.setDictionaryId(dictId);
										WordSentiment wordSentimentExisted = wordSentimentRepository
												.findByDictionaryIdAnd(dictId, wordProps[0].trim(), "",
														Double.parseDouble(wordProps[1]));
										if (null != wordSentimentExisted) {
											continue;
										}
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(wordProps[0].trim())
												.append(Double.parseDouble(wordProps[1])).append(dictId);
										valueS = stringBuilder.toString();
										if (!existList.contains(valueS)) {
											existList.add(valueS);
											wordSuccessNum += 1;
											sentimentWords.add(wordSentiment);
										}
									}
								} else {
									if (!isNum.matches()) {
										String wordName = wordProps[0];
										WordSentiment wordSentiment = new WordSentiment();
										wordSentiment.setDictionaryId(dictId);
										wordSentiment.setWord(wordName.trim());
										wordSentiment.setNature(wordProps[1].trim());
										WordSentiment wordSentimentExisted = wordSentimentRepository
												.findByDictionaryIds(dictId, wordName.trim(), wordProps[1].trim());
										if (null != wordSentimentExisted) {
											continue;
										}
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(dictId).append(wordName.trim()).append(wordProps[1].trim());
										valueS = stringBuilder.toString();
										if (!existList.contains(valueS)) {
											existList.add(valueS);
											wordSuccessNum += 1;
											sentimentWords.add(wordSentiment);
										}
										continue;
									}
									WordSentiment wordSentiment = new WordSentiment();
									wordSentiment.setWord(wordProps[0].trim());
									wordSentiment.setGrade(Double.parseDouble(wordProps[1]));
									wordSentiment.setDictionaryId(dictId);
									WordSentiment wordSentimentExisted = wordSentimentRepository.findByDictionaryIdAnd(
											dictId, wordProps[0].trim(), "", Double.parseDouble(wordProps[1]));
									if (null != wordSentimentExisted) {
										continue;
									}
									StringBuilder stringBuilder = new StringBuilder();
									stringBuilder.append(dictId).append(wordProps[0].trim())
											.append(Double.parseDouble(wordProps[1])).append(dictId);
									valueS = stringBuilder.toString();
									if (!existList.contains(valueS)) {
										existList.add(valueS);
										wordSuccessNum += 1;
										sentimentWords.add(wordSentiment);
									}
								}

								// String key =
								// DictionaryConstant.REDIS_WORD_PREFIX
								// + dictName + "_" + wordName;
								// Map<String, String> words = new
								// HashMap<>();
								// words.put(DictionaryConstant.REDIS_WORD_NAME,
								// StringUtil.avoidNull(wordName));
								// redisHandler.hmset(key, words);
							}
							if (wordProps.length == 1) {
								if (wordSentimentStrs != null && wordSentimentStrs.size() != 0) {
									if (!wordSentimentStrs.contains(wordProps[0])) {
										WordSentiment wordSentiment = new WordSentiment();
										wordSentiment.setDictionaryId(dictId);
										wordSentiment.setWord(wordProps[0].trim());
										WordSentiment wordSentimentExisted = wordSentimentRepository
												.findByDictionaryIds(dictId, wordProps[0].trim(), "");
										if (null != wordSentimentExisted) {
											continue;
										}
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(dictId).append(wordProps[0].trim());
										valueS = stringBuilder.toString();
										if (!existList.contains(valueS)) {
											existList.add(valueS);
											wordSuccessNum += 1;
											sentimentWords.add(wordSentiment);
										}
									}
								} else {
									WordSentiment wordSentiment = new WordSentiment();
									wordSentiment.setDictionaryId(dictId);
									wordSentiment.setWord(wordProps[0].trim());
									WordSentiment wordSentimentExisted = wordSentimentRepository
											.findByDictionaryIds(dictId, wordProps[0].trim(), "");
									if (null != wordSentimentExisted) {
										continue;
									}
									StringBuilder stringBuilder = new StringBuilder();
									stringBuilder.append(dictId).append(wordProps[0].trim());
									valueS = stringBuilder.toString();
									if (!existList.contains(valueS)) {
										existList.add(valueS);
										wordSuccessNum += 1;
										sentimentWords.add(wordSentiment);
									}
								}

								// String key =
								// DictionaryConstant.REDIS_WORD_PREFIX
								// + dictName + "_" + wordName;
								// Map<String, String> words = new
								// HashMap<>();
								// words.put(DictionaryConstant.REDIS_WORD_NAME,
								// StringUtil.avoidNull(wordName));
								// redisHandler.hmset(key, words);
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							LogManager.Exception(e);
							continue;
						}
					}
					if (sentimentWords != null && sentimentWords.size() != 0) {
						wordSentimentRepository.save(sentimentWords);
					}
				}
			} catch (NumberFormatException | IOException e) {
				LogManager.Exception(e);
			}
		}

		List<Integer> numList = new ArrayList<>();
		numList.add(wordNumber);
		numList.add(wordSuccessNum);

		return numList;
	}

	@Override
	public boolean stopWords(String dicId, List<String> ids) {
		Dictionary existedDict = dictionaryRepository.findOne(dicId);
		if (null == existedDict)
			return false;
		// IRedisHandler redisHandler =
		// RedisManager.getInstance().getStandaloneHandler();
		// String dictName = existedDict.getName();
		// 该词库是否在功能上启用，一旦启用则不能停用该词
		List<DicFuncRelation> existStatu = dicFuncRelationRepository.findAllByDicIdAndStatus(dicId, 1);
		if (existStatu == null || existStatu.size() == 0) {
			if ("map".equals(existedDict.getFormat())) {
				List<WordMap> mapWords = wordMapRepository.findAll(ids);
				// List<String> delWordKeys = new ArrayList<>();
				for (WordMap mapWord : mapWords) {
					mapWord.setWordStatus(0);
					// String wordKey = DictionaryConstant.REDIS_WORD_MAP_PREFIX
					// + dictName + "_" + mapWord.getWordKey();
					// delWordKeys.add(wordKey);
				}
				// if (!delWordKeys.isEmpty()) {
				// String[] delKyes = new String[delWordKeys.size()];
				// redisHandler.del(delWordKeys.toArray(delKyes));
				// }
				wordMapRepository.save(mapWords);
			}
			if ("ordinary".equals(existedDict.getFormat())) {
				List<WordOrdinary> ordinaryWords = wordOrdinaryRepository.findAll(ids);
				for (WordOrdinary ordinaryWord : ordinaryWords) {
					ordinaryWord.setWordStatus(0);
				}

				wordOrdinaryRepository.save(ordinaryWords);
			}
			if ("sentiment".equals(existedDict.getFormat())) {
				List<WordSentiment> wordSentiments = wordSentimentRepository.findAll(ids);
				for (WordSentiment wordSentiment : wordSentiments) {
					wordSentiment.setWordStatus(0);
				}

				wordSentimentRepository.save(wordSentiments);
			}

		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean startWords(String dicId, List<String> ids) {
		Dictionary existedDict = dictionaryRepository.findOne(dicId);
		if (null == existedDict)
			return false;
		// IRedisHandler redisHandler =
		// RedisManager.getInstance().getStandaloneHandler();
		// String dictName = existedDict.getName();
		// 该词库是否在功能上停用，一旦停用则不能启动该词
		List<DicFuncRelation> existStatu = dicFuncRelationRepository.findAllByDicIdAndStatus(dicId, 1);
		if (existStatu == null || existStatu.size() == 0) {
			if ("map".equals(existedDict.getFormat())) {
				List<WordMap> mapWords = wordMapRepository.findAll(ids);
				List<WordMap> mapWordList = new ArrayList<>();
				for (WordMap mapWord : mapWords) {
					mapWord.setWordStatus(1);
					mapWordList.add(mapWord);
					// String wordKey = mapWord.getWordKey();
					// String wordValue = mapWord.getWordValue();
					// String key = DictionaryConstant.REDIS_WORD_MAP_PREFIX +
					// dictName + "_" + wordKey;
					// Map<String, String> words = new HashMap<>();
					// words.put(DictionaryConstant.REDIS_WORD_MAP_KEY,
					// StringUtil.avoidNull(wordKey));
					// words.put(DictionaryConstant.REDIS_WORD_MAP_VALUE,
					// StringUtil.avoidNull(wordValue));
					// redisHandler.hmset(key, words);

				}
				wordMapRepository.save(mapWordList);
			}
			if ("ordinary".equals(existedDict.getFormat())) {
				List<WordOrdinary> ordinaryWords = wordOrdinaryRepository.findAll(ids);
				List<WordOrdinary> WordOrdinaryList = new ArrayList<>();
				for (WordOrdinary ordinaryWord : ordinaryWords) {
					ordinaryWord.setWordStatus(1);
					WordOrdinaryList.add(ordinaryWord);

				}
				wordOrdinaryRepository.save(WordOrdinaryList);
			}

			if ("sentiment".equals(existedDict.getFormat())) {
				List<WordSentiment> wordSentiments = wordSentimentRepository.findAll(ids);
				for (WordSentiment wordSentiment : wordSentiments) {
					wordSentiment.setWordStatus(1);
				}

				wordSentimentRepository.save(wordSentiments);
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public List<DicType> getAllDicType() {
		return dicTypeRepository.findAll();
	}

	@Override
	public boolean labelWord(String wordId, String labelIds) {
		List<LabelWordRelation> labelWordRelationsOld = labelWordRelationRepository.findAllByWordId(wordId);
		if (labelWordRelationsOld.size() > 0) {
			List<String> labelsOld = new ArrayList<>();
			for (LabelWordRelation labelWordRelation : labelWordRelationsOld) {
				labelWordRelationRepository.delete(labelWordRelation);
				labelsOld.add(labelWordRelation.getLabelId());
			}
			for (String labelId : labelsOld) {
				List<LabelWordRelation> labelsInfo = labelWordRelationRepository.findAllByLabelId(labelId);
				if (0 == labelsInfo.size()) {
					Label label = labelRepository.findOne(labelId);
					label.setStatus(0);
					labelRepository.save(label);
				}
			}
		}
		if (!StringUtil.isEmpty(labelIds)) {
			String[] labelIdArr = labelIds.split(",");
			for (String labelId : labelIdArr) {
				WordOrdinary wordOrdinary = wordOrdinaryRepository.findOne(wordId);
				if (null == wordOrdinary) {
					throw new AdminException("打标签失败，该词不存在");
				}
				Label label = labelRepository.findOne(labelId);
				if (null == label) {
					throw new AdminException("打标签失败，该标签不存在");
				}
				label.setStatus(1);
				labelRepository.save(label);
				LabelWordRelation labelWordRelation = new LabelWordRelation();
				labelWordRelation.setLabelId(labelId);
				labelWordRelation.setWordId(wordId);
				labelWordRelationRepository.save(labelWordRelation);
			}
		}

		return true;
	}

	@Override
	public List<Map<String, String>> labelsInfo(String wordId) {
		List<LabelWordRelation> labelWordRelations = labelWordRelationRepository.findAllByWordId(wordId);
		List<Map<String, String>> labels = new ArrayList<>();
		Map<String, String> labelMap = new HashMap<>();
		if (labelWordRelations.size() > 0) {
			for (LabelWordRelation labelWordRelation : labelWordRelations) {
				labelMap = new HashMap<>();
				labelMap.put("labelId", labelRepository.findOne(labelWordRelation.getLabelId()).getId());
				labelMap.put("labelName", labelRepository.findOne(labelWordRelation.getLabelId()).getName());
				labels.add(labelMap);
			}
		}

		return labels;
	}

}
