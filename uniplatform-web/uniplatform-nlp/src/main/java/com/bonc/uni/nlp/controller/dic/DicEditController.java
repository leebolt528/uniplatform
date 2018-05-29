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
import com.bonc.uni.nlp.dao.DicFuncRelationRepository;
import com.bonc.uni.nlp.entity.dic.DicFuncRelation;
import com.bonc.uni.nlp.entity.dic.Function;
import com.bonc.uni.nlp.service.IDicEditService;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年10月30日 下午6:38:55
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/dic/edit")
public class DicEditController {

	@Autowired
	IDicEditService taggedService;
	@Autowired
	DicFuncRelationRepository dicFuncRelationRepository;

	/**
	 * 编辑词典数据封装
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tagginfo", method = { RequestMethod.GET, RequestMethod.POST })
	public String editInfo(String dicId) {
		LogManager.Process("Process in controller: /nlap/admin/dic/edit/tagginfo");
		List<Function> beenFunction = new ArrayList<>();
		List<String> functionIds = taggedService.getFunctionId(dicId);
		if (functionIds != null && functionIds.size() != 0) {
			beenFunction = taggedService.getFunction(functionIds);
		}
		List<Function> functionAll = taggedService.getFunctionAll(dicId, functionIds);

		Map<String, Object> taggedResult = new HashMap<>();
		taggedResult.put("status", "200");
		taggedResult.put("msg", "获取该词库标签成功");
		taggedResult.put("beenFunction", beenFunction);
		taggedResult.put("functionAll", functionAll);

		return JSON.toJSONString(taggedResult, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 编辑词典
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tagged", method = { RequestMethod.GET, RequestMethod.POST })
	public String tagged(String dicId, @RequestParam(value = "newDictName", required = false) String newDictName,
			String tagIds) {
		LogManager.Process("Process in controller: /nlap/admin/dic/edit/tagged");
		Map<String, Object> taggedResult = new HashMap<>();
		/**
		 * 修改词库名称
		 */
		if (!StringUtil.isEmpty(newDictName)) {

			int typeAddSubTypes = taggedService.updateDictionaryInfo(dicId, newDictName);

			if (typeAddSubTypes == 0) {
				taggedResult.put("msg", "修改词库名称失败,该词库名称已存在！");
				taggedResult.put("status", "400");
				return JSON.toJSONString(taggedResult, SerializerFeature.DisableCircularReferenceDetect);
			}
		}
		// 已经打好的标签id
		if (StringUtil.isEmpty(tagIds.trim())) {
			taggedService.rmFunctiondAll(dicId);
			taggedResult.put("msg", "词典删除标签成功！");
			taggedResult.put("status", "200");

			LogManager.Process("Process out controller: /nlap/admin/tag/operation/tagged");

			return JSON.toJSONString(taggedResult, SerializerFeature.DisableCircularReferenceDetect);
		} else {

			int operation = taggedService.tagged(dicId, tagIds);
			if (operation != 0) {
				taggedResult.put("msg", "词典添加标签成功！");
				taggedResult.put("status", "200");
			} else {
				taggedResult.put("msg", "编辑词库失败！");
				taggedResult.put("status", "400");
			}

			LogManager.Process("Process out controller: /nlap/admin/tag/operation/tagged");

			return JSON.toJSONString(taggedResult, SerializerFeature.DisableCircularReferenceDetect);
		}

	}
}
