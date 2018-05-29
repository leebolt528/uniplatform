package com.bonc.uni.nlp.controller.dic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.service.IDictionaryManagementService;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * 
 * @author zlq
 *
 */
@RestController
@RequestMapping(value = "/nlap/admin/dicMgmt")
public class QueryDicController {

	@Autowired
	IDictionaryManagementService dicMgmtService;

	/**
	 * @Title: searchDicLibraryList
	 * @Description: load/search dictionary
	 * @param dicTypeId
	 * @param functionId
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/queryDic", method = { RequestMethod.GET, RequestMethod.POST })
	public String queryDic(@RequestParam(value = "dicTypeId", required = false) String dicTypeId,
			@RequestParam(value = "subTypeId", required = false) String subTypeId,
			@RequestParam(value = "functionId", required = false) String functionId,
			@RequestParam(value = "keyWord", required = false) String keyWord,
			@RequestParam(value = "pageIndex", required = false) Integer pageIndex,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		LogManager.Process("Process in controller: /nlap/admin/dicMgmt/queryDic");
		Map<String, Object> rsMap = new HashMap<>();
		List<Map<String, Object>> dictionaries = new ArrayList<>();
		List<Map<String, Object>> pagedDic = new ArrayList<>();
		if ("all".equals(functionId)) {
			functionId = null;
		}
		if ("all".equals(subTypeId)) {
			subTypeId = null;
		}
		if (StringUtil.isEmpty(dicTypeId) && StringUtil.isEmpty(functionId) && StringUtil.isEmpty(subTypeId)
				&& StringUtil.isEmpty(keyWord)) {
			dictionaries = dicMgmtService.queryAllDic();
		} else if (!StringUtil.isEmpty(keyWord)) {
			dictionaries = dicMgmtService.queryAllDicByKeyWord(dicTypeId, keyWord);
		} else {
			if (StringUtil.isEmpty(functionId)) {
				if (StringUtil.isEmpty(subTypeId)) {
					dictionaries = dicMgmtService.queryDicByType(dicTypeId);
				}
				if (StringUtil.isEmpty(dicTypeId)) {
					dictionaries = dicMgmtService.queryDicBySubType(subTypeId);
				}
				if (!StringUtil.isEmpty(subTypeId) && !StringUtil.isEmpty(dicTypeId)) {
					dictionaries = dicMgmtService.queryDicByTypeAndSubType(dicTypeId, subTypeId);
				}
			} else {
				if (StringUtil.isEmpty(subTypeId) && StringUtil.isEmpty(dicTypeId)) {
					dictionaries = dicMgmtService.queryDicByFunction(functionId);
				}
				if (StringUtil.isEmpty(subTypeId) && !StringUtil.isEmpty(dicTypeId)) {
					dictionaries = dicMgmtService.queryDicByTypeAndFunction(dicTypeId, functionId);
				}
				if (!StringUtil.isEmpty(subTypeId) && StringUtil.isEmpty(dicTypeId)) {

					dictionaries = dicMgmtService.queryDicBySubTypeAndFunctionl(subTypeId, functionId);
				}
				if (!StringUtil.isEmpty(subTypeId) && !StringUtil.isEmpty(dicTypeId)) {
					dictionaries = dicMgmtService.queryDicByTypeAndFunctionAndSubType(dicTypeId, functionId, subTypeId);
				}

			}
		}

		if (pageIndex == null || pageIndex < 1) {

			pageIndex = 1;
		}
		if (pageSize == null) {

			pageSize = 9;
		}
		int totalNumber = 0;
		if (dictionaries != null && dictionaries.size() > 0) {

			totalNumber = dictionaries.size();

			pagedDic = dictionaries.subList(pageSize * (pageIndex - 1),
					((pageSize * pageIndex) > totalNumber ? totalNumber : (pageSize * pageIndex)));

		}

		int totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : (totalNumber / pageSize) + 1;

		rsMap.put("msg", "查询成功");
		rsMap.put("status", "200");
		rsMap.put("dics", pagedDic);
		rsMap.put("totalPage", totalPage);
		rsMap.put("totalNumber", totalNumber);
		rsMap.put("prePage", pageIndex - 1);
		rsMap.put("curPage", pageIndex);
		rsMap.put("nextPage", pageIndex + 1);
		rsMap.put("firstPage", "1");
		rsMap.put("lastPage", totalPage);

		LogManager.Process("Process out controller: /nlap/admin/dicMgmt/queryDic");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

}
