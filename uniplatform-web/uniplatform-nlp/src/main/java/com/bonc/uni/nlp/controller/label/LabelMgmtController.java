package com.bonc.uni.nlp.controller.label;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.config.PosConfig;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.label.ILabelMgmtService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月5日 下午2:41:45
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/label/mgmt")
public class LabelMgmtController {
	@Autowired
	ILabelMgmtService labelMgmtService;

	@RequestMapping(value = "/labels/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String listLabels(String keyword, int pageIndex, int pageSize, boolean ascSort) {
		LogManager.Process("Process in : /nlap/admin/label/mgmt/labels/list");
		Map<String, Object> labelsMap = new HashMap<>();
		labelsMap.put("status", 200);
		labelsMap.put("msg", "获取标签列表成功");
		labelsMap.put("natureName", PosConfig.natureNameMap);

		try {
			Map<String, Object> labels = labelMgmtService.listLabels(keyword, pageIndex, pageSize, ascSort);
			labelsMap.put("labels", labels);
		} catch (AdminException e) {
			labelsMap.put("status", 400);
			labelsMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/label/mgmt/labels/list");
		return JSON.toJSONString(labelsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/label/add", method = { RequestMethod.GET, RequestMethod.POST })
	public String addLabel(String names) {
		LogManager.Process("Process in : /nlap/admin/label/mgmt/label/add");
		Map<String, Object> addLabelMap = new HashMap<>();
		addLabelMap.put("status", 200);
		addLabelMap.put("msg", "添加标签成功");

		try {
			labelMgmtService.addLabel(names);
		} catch (AdminException e) {
			addLabelMap.put("status", 400);
			addLabelMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/label/mgmt/label/add");
		return JSON.toJSONString(addLabelMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/label/edit", method = { RequestMethod.GET, RequestMethod.POST })
	public String editLabel(String labelId, String newNames) {
		LogManager.Process("Process in : /nlap/admin/label/mgmt/label/edit");
		Map<String, Object> editLabelMap = new HashMap<>();
		editLabelMap.put("status", 200);
		editLabelMap.put("msg", "修改标签成功");

		try {
			labelMgmtService.editLabel(labelId, newNames);
		} catch (AdminException e) {
			editLabelMap.put("status", 400);
			editLabelMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/label/mgmt/label/edit");
		return JSON.toJSONString(editLabelMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/labels/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteLabel(String labelIds) {
		LogManager.Process("Process in : /nlap/admin/label/mgmt/labels/delete");
		Map<String, Object> delLabelMap = new HashMap<>();
		delLabelMap.put("status", 200);
		delLabelMap.put("msg", "删除标签成功");

		try {
			labelMgmtService.delLabel(labelIds);
		} catch (AdminException e) {
			delLabelMap.put("status", 400);
			delLabelMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/label/mgmt/labels/delete");
		return JSON.toJSONString(delLabelMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/labels/info", method = { RequestMethod.GET, RequestMethod.POST })
	public String labelInfo(String labelId, String keyword, int pageIndex, int pageSize, boolean ascSort) {
		LogManager.Process("Process in : /nlap/admin/label/mgmt/labels/info");
		Map<String, Object> wordsMap = new HashMap<>();
		wordsMap.put("status", 200);
		wordsMap.put("msg", "获取标签下的词列表成功");

		try {
			Map<String, Object> words = labelMgmtService.labelInfo(labelId, keyword, pageIndex, pageSize, ascSort);
			wordsMap.put("words", words);
		} catch (AdminException e) {
			wordsMap.put("status", 400);
			wordsMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/label/mgmt/labels/info");
		return JSON.toJSONString(wordsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/label/download", method = { RequestMethod.GET, RequestMethod.POST })
	public synchronized String downloadLable(String labelId, String fileName, HttpServletResponse response) {
		LogManager.Process("Process in : /nlap/admin/label/mgmt/label/download");
		Map<String, Object> downloadLabelMap = new HashMap<>();
		downloadLabelMap.put("status", 200);
		downloadLabelMap.put("msg", "该标签下的词下载成功");
		if (null == labelId) {
			downloadLabelMap.put("status", "400");
			downloadLabelMap.put("msg", "请选择要导出的标签");
			return JSON.toJSONString(downloadLabelMap);
		}
		try { 
			labelMgmtService.downloadLable(labelId, fileName, response);
		} catch (AdminException e) {
			downloadLabelMap.put("status", 400);
			downloadLabelMap.put("msg", e.getMessage());
			return JSON.toJSONString(downloadLabelMap, SerializerFeature.DisableCircularReferenceDetect);
		}

		LogManager.Process("Process out : /nlap/admin/label/mgmt/label/download");
		return null;
	}

	@RequestMapping(value = "/labels/download", method = { RequestMethod.GET, RequestMethod.POST })
	public synchronized String downloadLabels(String labelIds, String fileName, HttpServletResponse response) {
		LogManager.Process("Process in : /nlap/admin/label/mgmt/labels/download");
		Map<String, Object> downloadLabelMap = new HashMap<>();
		downloadLabelMap.put("status", 200);
		downloadLabelMap.put("msg", "该标签下载成功");
		if (null == labelIds) {
			downloadLabelMap.put("status", "400");
			downloadLabelMap.put("msg", "请选择要导出的标签");
			return JSON.toJSONString(downloadLabelMap);
		}
		try {
			labelMgmtService.downloadLables(labelIds, fileName, response);
		} catch (AdminException e) {
			downloadLabelMap.put("status", 400);
			downloadLabelMap.put("msg", e.getMessage());
			return JSON.toJSONString(downloadLabelMap, SerializerFeature.DisableCircularReferenceDetect);
		}

		LogManager.Process("Process out : /nlap/admin/label/mgmt/labels/download");
		return null;
	}

	@RequestMapping(value = "/labels/upload", method = { RequestMethod.GET, RequestMethod.POST })
	public String uploadLabels(MultipartFile[] files) {
		LogManager.Process("Process in : /nlap/admin/label/mgmt/labels/upload");
		Map<String, Object> uploadLabelsMap = new HashMap<>();
		uploadLabelsMap.put("status", 200);

		try {
			List<Integer> num = labelMgmtService.uploadLabels(files);
			uploadLabelsMap.put("msg", "标签上传成功数为" + num.get(0) + "重复数为" + (num.get(1) - num.get(0)));
		} catch (AdminException e) {
			uploadLabelsMap.put("status", 400);
			uploadLabelsMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/label/mgmt/labels/upload");
		return JSON.toJSONString(uploadLabelsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
}
