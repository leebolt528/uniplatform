package com.bonc.uni.nlp.controller.dic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.config.PosConfig;
import com.bonc.uni.nlp.dao.DictionaryRepository;
import com.bonc.uni.nlp.dao.FieldTypeRepository;
import com.bonc.uni.nlp.entity.dic.Dictionary;
import com.bonc.uni.nlp.entity.dic.FieldType;
import com.bonc.uni.nlp.entity.dic.WordMap;
import com.bonc.uni.nlp.entity.dic.WordSentiment;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.IDicInfoService;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年10月26日 下午3:10:51
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/dicInfo")
public class DicInfoController {

	@Autowired
	IDicInfoService dicInfoService;
	@Autowired
	DictionaryRepository dictionaryRepository;
	@Autowired
	FieldTypeRepository fieldTypeRepository;

	/**
	 * 获取词库信息
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String dicInfo(String dicId, String searchWord, Integer pageIndex, Integer pageSize) {
		LogManager.Process("Process in controller: /nlap/admin/dicInfo/list");
		Map<String, Object> dicInfoResult = new HashMap<>();
		
		if (pageIndex == null || pageIndex < 1) {
			pageIndex = 1;
		}
		if (pageSize == null || pageSize < 1) {
			pageSize = 10;
		}
		Dictionary dictionary = dictionaryRepository.findOne(dicId);
		String fieldTypeName = null;
		String fieldTypeId = dictionary.getFieldTypeId();
		if (!StringUtil.isEmpty(fieldTypeId)) {
			FieldType fieldType = fieldTypeRepository.findOne(fieldTypeId);
			fieldTypeName = fieldType.getName();
		}
		List<Object> dicInfo = new ArrayList<>();
		dicInfoResult.put("msg", "获取词库列表信息成功！");
		dicInfoResult.put("status", "200");
		List<WordMap> mapDicInfo = new ArrayList<>();
		List<Object> ordDicInfo = new ArrayList<>();
		List<WordSentiment> sentimentDicInfo = new ArrayList<>();
		try {
			// 获取词典格式
			Dictionary dicFormat = dicInfoService.getDicFormat(dicId);
			String format = dicFormat.getFormat();
			int mapWordNumbers = 0;
			int ordinaryWordNumbers = 0;
			int sentimenWordNumbers = 0;
			if ("map".equals(format)) {
				mapDicInfo = dicInfoService.getMapDicInfo(dicId, searchWord, pageIndex, pageSize);
				mapWordNumbers = dicInfoService.getMapWordNumbers(dicId, searchWord);
			}
			if ("ordinary".equals(format)) {
				ordDicInfo = dicInfoService.getOrdinaryDicInfo(dicId, searchWord, pageIndex, pageSize);
				ordinaryWordNumbers = dicInfoService.getOrdinaryWordNumbers(dicId, searchWord);
			}
			if ("sentiment".equals(format)) {
				sentimentDicInfo = dicInfoService.getSentimentDicInfo(dicId, searchWord, pageIndex, pageSize);
				sentimenWordNumbers = dicInfoService.getSentimentWordNumbers(dicId, searchWord);
			}
			int totalWordNumber = mapWordNumbers + ordinaryWordNumbers + sentimenWordNumbers;
			dicInfo.addAll(mapDicInfo);
			dicInfo.addAll(ordDicInfo);
			dicInfo.addAll(sentimentDicInfo);
			dicInfoResult.put("fieldType", fieldTypeName);
			dicInfoResult.put("format", format);
			dicInfoResult.put("dicInfo", dicInfo);
			dicInfoResult.put("totalWordNumber", totalWordNumber);
			dicInfoResult.put("natureName", PosConfig.natureNameMap);
		} catch (AdminException e) {
			dicInfoResult.put("status", 400);
			dicInfoResult.put("msg", e.getMessage());
		}
		LogManager.Process("Process out controller: /nlap/admin/dicInfo/list");
		return JSON.toJSONString(dicInfoResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 增加map格式词
	 * 
	 * @return
	 */
	@RequestMapping(value = "/word/add/map", method = { RequestMethod.GET, RequestMethod.POST })
	public String addDicMapWord(String dicId, String wordKey, String wordValue) {
		LogManager.Process("Process in controller: /nlap/admin/dicInfo/word/add/map");
		Map<String, Object> addMapWordResult = new HashMap<>();

		boolean oeration = dicInfoService.addMapWord(dicId, wordKey, wordValue);
		if (oeration) {
			addMapWordResult.put("msg", "添加键值对格式词成功！");
			addMapWordResult.put("status", "200");
		} else {
			addMapWordResult.put("status", 400);
			addMapWordResult.put("msg", "添加失败，该词已经存在！");
		}
		LogManager.Process("Process out controller: /nlap/admin/dicInfo/word/add/map");
		return JSON.toJSONString(addMapWordResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 增加ordinary格式词 TODO 修改接口 领域词库人 labelName
	 * 
	 * @return
	 */
	@RequestMapping(value = "/word/add/ordinary", method = { RequestMethod.GET, RequestMethod.POST })
	public String addOrdinaryDicWord(String dicId, String word,
			@RequestParam(value = "wordLabel", required = false) String wordLabel,
			@RequestParam(value = "nature", required = false) String nature, int frequency) {

		LogManager.Process("Process in controller: /nlap/admin/dicInfo/word/add/ordinary");
		Map<String, Object> addOrdWordResult = new HashMap<>();
		addOrdWordResult.put("msg", "添加普通词成功！");
		addOrdWordResult.put("status", "200 ");

		try {
			dicInfoService.addOrdWord(dicId, word, wordLabel, nature, frequency);
		} catch (AdminException e) {
			addOrdWordResult.put("status", 400);
			addOrdWordResult.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller: /nlap/admin/dicInfo/word/add/ordinary");
		return JSON.toJSONString(addOrdWordResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 增加sentiment格式词
	 * 
	 * @return
	 */
	@RequestMapping(value = "/word/add/sentiment", method = { RequestMethod.GET, RequestMethod.POST })
	public String addSentimentDicWord(String dicId, String word,
			@RequestParam(value = "nature", required = false) String nature, Double grade) {

		LogManager.Process("Process in controller: /nlap/admin/dicInfo/word/add/sentiment");
		Map<String, Object> addSentimentWordResult = new HashMap<>();
		boolean operation = dicInfoService.addSentimentWord(dicId, word, nature, grade);

		if (operation) {
			addSentimentWordResult.put("msg", "添加情感词成功！");
			addSentimentWordResult.put("status", "200 ");
		} else {
			addSentimentWordResult.put("status", 400);
			addSentimentWordResult.put("msg", "添加失败，该词已经存在！");
		}
		LogManager.Process("Process out controller: /nlap/admin/dicInfo/word/add/ordinary");
		return JSON.toJSONString(addSentimentWordResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 删除词
	 * 
	 * @return
	 */
	@RequestMapping(value = "/word/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteDicWord(String dicId, String wordIds) {
		LogManager.Process("Process in controller: /nlap/admin/dicInfo/word/delete");
		Map<String, Object> deleteDicWord = new HashMap<>();
		if (wordIds.contains(",") && wordIds.endsWith(",")) {
			wordIds = wordIds.substring(0, wordIds.length() - 1);
		}
		String[] arrIds = wordIds.split(",");
		List<String> ids = new ArrayList<>();
		for (String id : arrIds) {
			ids.add(id);
		}
		boolean operation = dicInfoService.deleteWord(dicId, ids);
		if (operation) {
			deleteDicWord.put("msg", "删除词成功！");
			deleteDicWord.put("status", "200");
		} else {
			deleteDicWord.put("status", 400);
			deleteDicWord.put("msg", "该词被功能应用，删除失败！");
		}
		LogManager.Process("Process out controller: /nlap/admin/dicInfo/word/delete");
		return JSON.toJSONString(deleteDicWord, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 修改map词
	 * 
	 * @return
	 */
	@RequestMapping(value = "/word/edit/map", method = { RequestMethod.GET, RequestMethod.POST })
	public String editMapWord(String dicId, String wordId, String newKey, String newValue) {
		LogManager.Process("Process in controller: /nlap/admin/dicInfo/word/edit/map");
		Map<String, Object> updateResult = new HashMap<>();
		updateResult.put("msg", "修改键值对格式词成功！");
		updateResult.put("status", "200");
		try {
			dicInfoService.editMapWord(dicId, wordId, newKey, newValue);
		} catch (Exception e) {
			updateResult.put("status", "400");
			updateResult.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller: /nlap/admin/dicInfo/word/edit/map");
		return JSON.toJSONString(updateResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 修改ordinary词
	 * 
	 * @return
	 */
	// TODO 修改领域词库人名
	@RequestMapping(value = "/word/edit/ordinary", method = { RequestMethod.GET, RequestMethod.POST })
	public String editOrdinaryWord(String dicId, String wordId, String newWord,
			@RequestParam(value = "wordLabel", required = false) String wordLabel, String newNature, int newFrequency) {
		LogManager.Process("Process in controller: /nlap/admin/dicInfo/word/edit/ordinary");
		Map<String, Object> updateResult = new HashMap<>();
		updateResult.put("msg", "修改普通词成功！");
		updateResult.put("status", "200");
		try {
			dicInfoService.editOrdinaryWord(dicId, wordId, newWord, wordLabel, newNature, newFrequency);
		} catch (AdminException e) {
			updateResult.put("status", "400");
			updateResult.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller: /nlap/admin/dicInfo/word/edit/ordinary");
		return JSON.toJSONString(updateResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 修改seniment词
	 * 
	 * @return
	 */
	@RequestMapping(value = "/word/edit/sentiment", method = { RequestMethod.GET, RequestMethod.POST })
	public String editSenimentWord(String dicId, String wordId, String newWord, String newNature, Double grade) {
		LogManager.Process("Process in controller: /nlap/admin/dicInfo/word/edit/sentiment");
		Map<String, Object> updateResult = new HashMap<>();
		updateResult.put("msg", "修改情感词成功！");
		updateResult.put("status", "200");
		try {
			dicInfoService.editSentimentWord(dicId, wordId, newWord, newNature, grade);
		} catch (Exception e) {
			updateResult.put("status", "400");
			updateResult.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller: /nlap/admin/dicInfo/word/edit/ordinary");
		return JSON.toJSONString(updateResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 上传词典
	 */
	@RequestMapping(value = "/word/upload", method = { RequestMethod.GET, RequestMethod.POST })
	public String dicUpload(String dictId, @RequestParam("file") MultipartFile[] file) {
		LogManager.Process("Process in controller: /nlap/admin/dicInfo/word/upload");
		Map<String, Object> uploadResult = new HashMap<>();
		if (file == null) {
			uploadResult.put("status", 400);
			uploadResult.put("msg", "上传失败,文件不存在");
			return JSON.toJSONString(uploadResult, SerializerFeature.DisableCircularReferenceDetect);
		}
		uploadResult.put("status", "200");
		uploadResult.put("msg", "上传成功");
		try {
			List<Integer> wordsNumList = dicInfoService.uploadWords2Dict(dictId, file);
			uploadResult.put("msg",
					"上传词数为" + wordsNumList.get(0) + "，成功数为" + wordsNumList.get(1) + "，重复数为"
							+ ((wordsNumList.get(0) - wordsNumList.get(1)) > 0
									? (wordsNumList.get(0) - wordsNumList.get(1)) : 0));
		} catch (AdminException e) {
			uploadResult.put("status", 400);
			uploadResult.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller: /nlap/admin/dicInfo/word/upload");
		return JSON.toJSONString(uploadResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 停用词
	 */
	@RequestMapping(value = "/word/stop", method = { RequestMethod.GET, RequestMethod.POST })
	public String stopWords(String dicId, String wordIds) {
		LogManager.Process("Process in controller: /nlap/admin/dicInfo/word/stop");
		Map<String, Object> result = new HashMap<>();
		if (wordIds.contains(",") && wordIds.endsWith(",")) {
			wordIds = wordIds.substring(0, wordIds.length() - 1);
		}
		String[] arrIds = wordIds.split(",");
		List<String> ids = new ArrayList<>();
		for (String id : arrIds) {
			ids.add(id);
		}
		boolean operation = dicInfoService.stopWords(dicId, ids);
		if (operation) {
			result.put("status", 200);
			result.put("msg", "停用该词成功");
		} else {
			result.put("status", 400);
			result.put("msg", "该词已被应用，停用失败");
		}
		LogManager.Process("Process out controller: /nlap/admin/dicInfo/word/stop");
		return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 启用词
	 */
	@RequestMapping(value = "/word/start", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String dicWordStart(String dicId, String wordIds) {
		LogManager.Process("Process in controller: /nlap/admin/dicInfo/word/start");

		Map<String, Object> result = new HashMap<>();
		if (wordIds.contains(",") && wordIds.endsWith(",")) {
			wordIds = wordIds.substring(0, wordIds.length() - 1);
		}
		String[] arrIds = wordIds.split(",");
		List<String> ids = new ArrayList<>();
		for (String id : arrIds) {
			ids.add(id);
		}
		boolean operation = dicInfoService.startWords(dicId, ids);
		if (operation) {
			result.put("status", 200);
			result.put("msg", "启用该词成功");
		} else {
			result.put("status", 400);
			result.put("msg", "该词已被应用，启用失败");
		}
		LogManager.Process("Process out controller: /nlap/admin/dicInfo/word/start");
		return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/word/label", method = { RequestMethod.GET, RequestMethod.POST })
	public String labelWord(String wordId, @RequestParam(value = "labelIds", required = false) String labelIds) {
		LogManager.Process("Process in controller:  nlap/admin/dicInfo/word/label");
		Map<String, Object> labelDicMap = new HashMap<>();
		labelDicMap.put("status", "200");
		labelDicMap.put("msg", "给词打标签成功");
		try {
			dicInfoService.labelWord(wordId, labelIds);
		} catch (AdminException e) {
			labelDicMap.put("status", "400");
			labelDicMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller:  nlap/admin/dicInfo/word/label");
		return JSON.toJSONString(labelDicMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/word/labels/info", method = { RequestMethod.GET, RequestMethod.POST })
	public String labelWordInfo(String wordId) {
		LogManager.Process("Process in controller:  nlap/admin/dicInfo/word/labels/info");
		Map<String, Object> labelsMap = new HashMap<>();
		labelsMap.put("status", "200");
		labelsMap.put("msg", "获取该词下的标签列表成功");
		try {
			List<Map<String, String>> lables = dicInfoService.labelsInfo(wordId);
			labelsMap.put("lables", lables);
		} catch (AdminException e) {
			labelsMap.put("status", "400");
			labelsMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller:  nlap/admin/dicInfo/word/labels/info");
		return JSON.toJSONString(labelsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
}
