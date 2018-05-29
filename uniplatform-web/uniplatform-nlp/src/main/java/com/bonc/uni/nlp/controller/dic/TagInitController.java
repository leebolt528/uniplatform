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
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.entity.dic.DicType;
import com.bonc.uni.nlp.service.IDicStatusService;
import com.bonc.uni.nlp.service.ITagService;
import com.bonc.uni.nlp.utils.ListTailUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年10月25日 下午4:27:47
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/tag/init")
public class TagInitController {

	@Autowired
	ITagService tagService;
	@Autowired
	IDicStatusService dicStatusService;

	@RequestMapping(value = "/init/label", method = { RequestMethod.GET, RequestMethod.POST })
	public String listTags() {
		LogManager.Process("Process in controller: /nlap/admin/tag/init/init/label");

		Map<String, Object> labelResult = new HashMap<>();
		labelResult.put("msg", "词典标签初始化成功！");
		labelResult.put("status", "200");

		// try {
		// 获取词典类型
		List<DicType> dicTypes = tagService.getTagsList();
		List<Map<String, Object>> labelsList = new ArrayList<>();
		List<Map<String, Object>> resultList = new ArrayList<>();
		int totalNumber = 0;

		List<Map<String, Object>> subTypesAll = new ArrayList<>();
		subTypesAll.add(tagService.getSubTypes());
		for (int i = 0; i < dicTypes.size(); i++) {
			String typeId = dicTypes.get(i).getId();
			// 获取词典数
			int dicNumbers = tagService.getDicNumbers(typeId);

			// 获取对应词典类型下的功能id

			List<Map<String, Object>> subTypeBytype = tagService.getSubTypes(typeId);
			subTypesAll = ListTailUtil.tailList(subTypesAll, subTypeBytype);
			totalNumber += dicNumbers;
			String typeName = dicTypes.get(i).getDisplayName();

			Map<String, Object> labels = new HashMap<>();
			labels.put("dicTypeId", typeId);
			labels.put("dicTypeName", typeName);
			labels.put("dicNumber", dicNumbers);
			labels.put("subTypes", subTypeBytype);
			labelsList.add(labels);
		}

		Map<String, Object> dicsMap = new HashMap<>();
		dicsMap.put("dicTypeId", "");
		dicsMap.put("dicTypeName", "全部词库");
		dicsMap.put("subTypes", subTypesAll);
		dicsMap.put("dicNumber", totalNumber);
		resultList.add(dicsMap);
		resultList.addAll(labelsList);
		labelResult.put("labels", resultList);

		LogManager.Process("Process out controller: /nlap/admin/tag/init/init/label");
		return JSON.toJSONString(labelResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 词库标签启动信息 (已启动，未启动)
	 * 
	 * @param dicId
	 * @return
	 */
	@RequestMapping(value = "/label/dicLabel", method = { RequestMethod.GET, RequestMethod.POST })
	public String getDicLabel(@RequestParam(value = "dicId", required = true) String dicId) {
		LogManager.Process("Process in controller: /nlap/admin/tag/init/label/dicLabel");

		Map<String, Object> rsMap = new HashMap<>();
		List<Map<String, String>> enabledFunction = tagService.getEnabledFunction(dicId);
		List<Map<String, String>> disabledFunction = tagService.getDisabledFunction(dicId);
		rsMap.put("msg", "查询成功");
		rsMap.put("status", "200");
		rsMap.put("enabledFunction", enabledFunction);
		rsMap.put("disabledFunction", disabledFunction);

		LogManager.Process("Process out controller: /nlap/admin/tag/init/label/dicLabel");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 词库标签启动||停用
	 * 
	 * @param dicId
	 * @return
	 */
	@RequestMapping(value = "/label/dicEnable", method = { RequestMethod.GET, RequestMethod.POST })
	public String dicEnable(@RequestParam(value = "dicId", required = true) String dicId,
			@RequestParam(value = "selectFunctionsId", required = false) String selectFunctionsId) {
		LogManager.Process("Process in controller: /nlap/admin/tag/init/label/dicEnable");

		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("msg", "启用成功");
		rsMap.put("status", 200);

		if (null == selectFunctionsId || "".equals(selectFunctionsId)) {
			tagService.disableAllFunction(dicId);

			rsMap.put("msg", "所有标签都已停用");
			rsMap.put("status", 200);
			return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);

		}
		String[] functionId = selectFunctionsId.trim().split(",");
		tagService.enableDic(dicId, functionId);

		LogManager.Process("Process out controller: /nlap/admin/tag/init/label/dicEnable");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

}
