package com.bonc.uni.nlp.controller.dic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.config.PosConfig;
import com.bonc.uni.nlp.dao.FieldTypeRepository;
import com.bonc.uni.nlp.dao.WordMapRepository;
import com.bonc.uni.nlp.dao.WordOrdinaryRepository;
import com.bonc.uni.nlp.dao.WordSentimentRepository;
import com.bonc.uni.nlp.dao.label.LabelRepository;
import com.bonc.uni.nlp.dao.label.LabelWordRelationRepository;
import com.bonc.uni.nlp.entity.dic.FieldType;
import com.bonc.uni.nlp.entity.dic.WordMap;
import com.bonc.uni.nlp.entity.dic.WordOrdinary;
import com.bonc.uni.nlp.entity.dic.WordSentiment;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.IDictionaryManagementService;
import com.bonc.uni.nlp.utils.Encoding;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : XieMingYuan
 * @version: 2017年10月30日 上午11:26:37
 */
@RestController
@CrossOrigin
@RequestMapping("/nlap/admin/dicMgmt")
public class DicManagementController {

	@Autowired
	IDictionaryManagementService dictService;
	@Autowired
	WordMapRepository wordMapRepository;
	@Autowired
	WordOrdinaryRepository wordOrdinaryRepository;
	@Autowired
	WordSentimentRepository wordSentimentRepository;
	@Autowired
	FieldTypeRepository fieldTypeRepository;
	@Autowired
	LabelRepository labelRepository;
	@Autowired
	LabelWordRelationRepository labelWordRelationRepository;

	/**
	 * 创建词库时获取类型与子类型对用关系
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dict/info", method = { RequestMethod.GET, RequestMethod.POST })
	public String intoAddDictionary() {
		LogManager.Process("Process in controller:  nlap/admin/dicMgmt/dic/info");

		Map<String, Object> uploadResult = new HashMap<>();
		uploadResult.put("status", "200");
		uploadResult.put("msg", "获取信息成功");

		List<Map<String, Object>> typeAddSubTypes = dictService.intoAddDictionary();
		if (typeAddSubTypes.size() <= 0) {
			uploadResult.put("status", "400");
			uploadResult.put("msg", "上传失败");
			return JSON.toJSONString(uploadResult, SerializerFeature.DisableCircularReferenceDetect);

		}
		uploadResult.put("typeAddSubTypes", typeAddSubTypes);
		LogManager.Process("Process out controller:  nlap/admin/dicMgmt/dic/info");
		return JSON.toJSONString(uploadResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 创建词库
	 * 
	 * @param dictName
	 *            词库名称
	 * @param dictTypeId
	 *            词库类型
	 * @param dicSubTypeId
	 *            词库子类型
	 * @param wordOrdinarys
	 *            通用类型词
	 * @param wordMaps
	 *            map类型词
	 * @param format
	 *            词库中词的类型（map,ordinarys）
	 * @return
	 */
	@RequestMapping(value = "/dict/add", method = { RequestMethod.GET, RequestMethod.POST })
	public String addDictionary(String dicName, String dicTypeid, String dicSubTypeId,
			@RequestParam(value = "functionId", required = false) String functionId,
			@RequestParam(value = "files", required = false) MultipartFile[] files, String dicFormat) {

		LogManager.Process("Process in controller: nlap/admin/dicMgmt/dic/add");
		int wordAllNum = 0;
		int mapWordSucceedAllNum = 0;
		int ordinaryWordSucceedAllNum = 0;
		int sentimentWordSucceedAllNum = 0;
		Map<String, Object> uploadResult = new HashMap<>();
		uploadResult.put("status", "200");
		uploadResult.put("msg", "上传成功");

		List<WordOrdinary> wordOrdinarys = new ArrayList<>();
		List<WordMap> wordMaps = new ArrayList<>();
		List<WordSentiment> wordSentiments = new ArrayList<>();
		List<String> existList = new ArrayList<>();

		if (null != files) {
			String valueS = null;
			for (MultipartFile file : files) {
				if (null == file) {
					continue;
				}
				try (InputStream in = file.getInputStream();
						InputStreamReader inReader = new InputStreamReader(in);
						BufferedReader reader = new BufferedReader(inReader)) {
					if (!"UTF-8".equals(Encoding.tryEncoding(file))) {
						uploadResult.put("status", 400);
						uploadResult.put("msg", "请上传Utf-8");
						LogManager.Debug("Not file charset!");
						return JSON.toJSONString(uploadResult, SerializerFeature.DisableCircularReferenceDetect);
					}
					String line = null;
					if ("sentiment".equals(dicFormat)) {
						while (null != (line = reader.readLine())) {
							if (line.isEmpty()) {
								continue;
							}
							wordAllNum += 1;
							String[] infos = line.split("，|,");
							if (infos.length == 3) {
								if ("=".equals(infos[1]) || infos[1].contains("=")) {
									continue;
								}
								WordSentiment wordSentiment = new WordSentiment();
								wordSentiment.setWord(infos[0].trim());
								wordSentiment.setNature(infos[1].trim());
								wordSentiment.setGrade(Double.parseDouble(infos[2]));
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(infos[0].trim()).append(infos[1].trim())
										.append(Double.parseDouble(infos[2]));
								valueS = stringBuilder.toString();
								if (!existList.contains(valueS)) {
									existList.add(valueS);
									sentimentWordSucceedAllNum += 1;
									wordSentiments.add(wordSentiment);
								}
							}
							if (infos.length == 1) {
								String wordName = infos[0].trim();
								WordSentiment wordSentiment = new WordSentiment();
								wordSentiment.setWord(wordName);
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(wordName);
								valueS = stringBuilder.toString();
								if (!existList.contains(valueS)) {
									existList.add(valueS);
									sentimentWordSucceedAllNum += 1;
									wordSentiments.add(wordSentiment);
								}
							}
							if (infos.length == 2) {
								Pattern pattern = Pattern.compile("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+");
								Matcher isNum = pattern.matcher(infos[1]);
								if (isNum.matches()) {
									String wordName = infos[0].trim();
									WordSentiment wordSentiment = new WordSentiment();
									wordSentiment.setWord(wordName);
									wordSentiment.setNature(infos[1].trim());
									StringBuilder stringBuilder = new StringBuilder();
									stringBuilder.append(wordName).append(infos[1].trim());
									valueS = stringBuilder.toString();
									if (!existList.contains(valueS)) {
										existList.add(valueS);
										sentimentWordSucceedAllNum += 1;
										wordSentiments.add(wordSentiment);
									}
									continue;
								}
								try {
									Double.parseDouble(infos[1]);
								} catch (Exception e) {
									continue;
								}
								String wordName = infos[0].trim();
								WordSentiment wordSentiment = new WordSentiment();
								wordSentiment.setWord(wordName);
								wordSentiment.setGrade(Double.parseDouble(infos[1]));
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(wordName).append(Double.parseDouble(infos[1]));
								valueS = stringBuilder.toString();
								if (!existList.contains(valueS)) {
									existList.add(valueS);
									sentimentWordSucceedAllNum += 1;
									wordSentiments.add(wordSentiment);
								}
							} else {
								continue;
							}
						}
					} else if ("ordinary".equals(dicFormat)) {
						while (null != (line = reader.readLine())) {
							if (line.isEmpty()) {
								continue;
							}
							wordAllNum += 1;
							String[] infos = line.split("，|,");
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
								WordOrdinary wordOrdinary = new WordOrdinary();
								wordOrdinary.setWord(infos[0].trim());
								wordOrdinary.setNature(infos[1]);
								wordOrdinary.setFrequency(Integer.parseInt(infos[2]));
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(infos[0].trim()).append(infos[1])
										.append(Integer.parseInt(infos[2]));
								valueS = stringBuilder.toString();
								if (!existList.contains(valueS)) {
									existList.add(valueS);
									ordinaryWordSucceedAllNum += 1;
									wordOrdinarys.add(wordOrdinary);
								}
							}
							if (infos.length == 1) {
								String wordName = infos[0].trim();
								WordOrdinary wordOrdinary = new WordOrdinary();
								wordOrdinary.setWord(wordName);
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(wordName).append(0);
								valueS = stringBuilder.toString();
								if (!existList.contains(valueS)) {
									existList.add(valueS);
									ordinaryWordSucceedAllNum += 1;
									wordOrdinarys.add(wordOrdinary);
								}
							}
							if (infos.length == 2) {
								if (null != PosConfig.natureNameMap.get(infos[1])) {
									String wordName = infos[0].trim();
									WordOrdinary wordOrdinary = new WordOrdinary();
									wordOrdinary.setWord(wordName);
									wordOrdinary.setNature(infos[1]);
									StringBuilder stringBuilder = new StringBuilder();
									stringBuilder.append(wordName).append(infos[1]).append(0);
									valueS = stringBuilder.toString();
									if (!existList.contains(valueS)) {
										existList.add(valueS);
										ordinaryWordSucceedAllNum += 1;
										wordOrdinarys.add(wordOrdinary);
									}
									continue;
								}
								try {
									Integer.parseInt(infos[1]);
								} catch (Exception e) {
									continue;
								}
								String wordName = infos[0].trim();
								WordOrdinary wordOrdinary = new WordOrdinary();
								wordOrdinary.setWord(wordName);
								wordOrdinary.setFrequency(Integer.parseInt(infos[1]));
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(wordName).append(Integer.parseInt(infos[1]));
								valueS = stringBuilder.toString();
								if (!existList.contains(valueS)) {
									existList.add(valueS);
									ordinaryWordSucceedAllNum += 1;
									wordOrdinarys.add(wordOrdinary);
								}
							} else {
								continue;
							}
						}
					} else if ("map".equals(dicFormat)) {
						while (null != (line = reader.readLine())) {
							if (line.isEmpty()) {
								continue;
							}
							wordAllNum += 1;
							String[] infos = line.split("=");
							if (2 != infos.length)
								continue;
							String wordName = infos[0].trim();
							String wordValue = infos[1];
							wordValue = wordValue.replaceAll("(,|，)", ",");
							WordMap wordMap = new WordMap();
							wordMap.setWordKey(wordName);
							wordMap.setWordValue(wordValue);
							StringBuilder stringBuilder = new StringBuilder();
							stringBuilder.append(wordName).append(wordValue);
							valueS = stringBuilder.toString();
							if (!existList.contains(valueS)) {
								existList.add(valueS);
								mapWordSucceedAllNum += 1;
								wordMaps.add(wordMap);
							}
						}
					} else {
						uploadResult.put("status", "400");
						uploadResult.put("msg", "词库类型传输错误");
					}
				} catch (IOException e) {
					LogManager.Exception("", e);
				}
			}
		}

		int addStatus = 0;
		try {
			addStatus = dictService.addDictionary(dicName, dicTypeid, dicSubTypeId, wordOrdinarys, wordMaps,
					wordSentiments, dicFormat, functionId);
		} catch (Exception e) {
			LogManager.Exception("add dic Exception", e);
		}
		if (addStatus <= -1) {
			uploadResult.put("status", "400");
			uploadResult.put("msg", "词库名称已存在");
			return JSON.toJSONString(uploadResult, SerializerFeature.DisableCircularReferenceDetect);

		}
		if (addStatus <= 0) {
			uploadResult.put("status", "400");
			uploadResult.put("msg", "创建词库失败");
			return JSON.toJSONString(uploadResult, SerializerFeature.DisableCircularReferenceDetect);

		}
		if ("ordinary".equals(dicFormat)) {
			uploadResult.put("wordAllNum", "普通词上传数为" + wordAllNum + "，成功数为" + ordinaryWordSucceedAllNum + "，重复数为"
					+ (((wordAllNum - ordinaryWordSucceedAllNum) > 0) ? (wordAllNum - ordinaryWordSucceedAllNum) : 0));
		} else if ("map".equals(dicFormat)) {
			uploadResult.put("wordAllNum", "键值对词上传数为" + wordAllNum + "，成功数为" + mapWordSucceedAllNum + "，重复数为"
					+ (((wordAllNum - mapWordSucceedAllNum) > 0) ? (wordAllNum - mapWordSucceedAllNum) : 0));
		} else {
			uploadResult.put("wordAllNum",
					"情感词上传数为" + wordAllNum + "，成功数为" + sentimentWordSucceedAllNum + "，重复数为"
							+ (((wordAllNum - sentimentWordSucceedAllNum) > 0)
									? (wordAllNum - sentimentWordSucceedAllNum) : 0));
		}

		LogManager.Process("Process out controller:  nlap/admin/dicMgmt/dic/add");
		return JSON.toJSONString(uploadResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/field/dict/add", method = { RequestMethod.GET, RequestMethod.POST })
	public String addFieldDictionary(String dicName, String dicTypeid, String dicSubTypeId,
			@RequestParam(value = "fieldTypeId", required = false) String fieldTypeId,
			@RequestParam(value = "functionId", required = false) String functionId,
			@RequestParam(value = "files", required = false) MultipartFile[] files, String dicFormat) {
		LogManager.Process("Process in controller: nlap/admin/dicMgmt/field/dict/add");

		Map<String, Object> addResult = new HashMap<>();
		addResult.put("status", "200");
		addResult.put("msg", "词库创建成功");

		try {
			List<Integer> number = dictService.addFieldDictionary(dicName, dicTypeid, dicSubTypeId, fieldTypeId, files,
					dicFormat, functionId);
			addResult.put("info", "上传词成功数为" + number.get(0) + "，重复数为"
					+ ((number.get(0) - number.get(1)) > 0 ? number.get(0) - number.get(1) : 0));
		} catch (AdminException e) {
			addResult.put("status", "400");
			addResult.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller:  nlap/admin/dicMgmt/field/dict/add");
		return JSON.toJSONString(addResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/update/name", method = { RequestMethod.GET, RequestMethod.POST })
	public String updateDictionaryName(String dictId, String newDicName) {
		LogManager.Process("Process in controller: nlap/admin/dicMgmt/update/name");
		Map<String, Object> editResult = new HashMap<>();
		editResult.put("status", "200");
		editResult.put("msg", "词库名称修改成功");

		try {
			dictService.updateDictionaryName(dictId, newDicName);
		} catch (AdminException e) {
			editResult.put("status", "400");
			editResult.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller: nlap/admin/dicMgmt/update/name");
		return JSON.toJSONString(editResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 删除词库
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dict/remove", method = { RequestMethod.GET, RequestMethod.POST })
	public String removeDictionary(String dictId) {
		LogManager.Process("Process in controller:  nlap/admin/dicMgmt/dict/remove");

		Map<String, Object> uploadResult = new HashMap<>();
		uploadResult.put("status", "200");
		uploadResult.put("msg", "刪除成功");

		try {
			dictService.removeDictionary(dictId);
		} catch (AdminException e) {
			uploadResult.put("status", "400");
			uploadResult.put("msg", e.getMessage());
		}
		LogManager.Process("Process out controller:nlap/admin/dicMgmt/dict/remove");
		return JSON.toJSONString(uploadResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/field/type/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String listFieldType(String typeId, String subTypeId) {
		LogManager.Process("Process in controller:  nlap/admin/dicMgmt/field/type/lis");
		Map<String, Object> fieldTypesMap = new HashMap<>();
		fieldTypesMap.put("status", "200");
		fieldTypesMap.put("msg", "查询领域词库功能分类列表成功");
		try {
			List<FieldType> fieldTypes = dictService.listFieldType(typeId, subTypeId);
			fieldTypesMap.put("fieldTypes", fieldTypes);
		} catch (AdminException e) {
			fieldTypesMap.put("status", "400");
			fieldTypesMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller:  nlap/admin/dicMgmt/field/type/lis");
		return JSON.toJSONString(fieldTypesMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/dic/label", method = { RequestMethod.GET, RequestMethod.POST })
	public String labelDic(String dicId, @RequestParam(value = "labelIds", required = false) String labelIds) {
		LogManager.Process("Process in controller:  nlap/admin/dicMgmt/dic/label");
		Map<String, Object> labelDicMap = new HashMap<>();
		labelDicMap.put("status", "200");
		labelDicMap.put("msg", "给词库打标签成功");
		try {
			dictService.labelDic(dicId, labelIds);
		} catch (AdminException e) {
			labelDicMap.put("status", "400");
			labelDicMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller:  nlap/admin/dicMgmt/dic/label");
		return JSON.toJSONString(labelDicMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/dic/labels/info", method = { RequestMethod.GET, RequestMethod.POST })
	public String labelDicInfo(String dicId) {
		LogManager.Process("Process in controller:  nlap/admin/dicMgmt/dic/labels/info");
		Map<String, Object> labelsMap = new HashMap<>();
		labelsMap.put("status", "200");
		labelsMap.put("msg", "获取该词典下的标签列表成功");
		try {
			List<Map<String, String>> lables = dictService.labelsInfo(dicId);
			labelsMap.put("lables", lables);
		} catch (AdminException e) {
			labelsMap.put("status", "400");
			labelsMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller:  nlap/admin/dicMgmt/dic/labels/info");
		return JSON.toJSONString(labelsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

}
