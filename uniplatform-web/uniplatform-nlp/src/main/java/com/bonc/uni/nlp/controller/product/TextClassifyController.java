package com.bonc.uni.nlp.controller.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.entity.product.ClassifyMultiResult;
import com.bonc.uni.nlp.entity.product.RuleClassifyResult;
import com.bonc.uni.nlp.service.product.ITextClassifyService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月23日 下午4:12:11
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/text/classify")
public class TextClassifyController {

	@Autowired
	ITextClassifyService classifyService;

	@RequestMapping(value = "/text", method = { RequestMethod.GET, RequestMethod.POST })
	public String classify(String text) {
		LogManager.Process("Process in controller: /nlap/admin/text/classify/text");
		
		String classifyResult = classifyService.svmClassify(text);

		LogManager.Process("Process out controller: /nlap/admin/text/classify/text");
		
		return classifyResult;
	}
	
	@RequestMapping(value = "/text/en", method = { RequestMethod.GET, RequestMethod.POST })
	public String classifyEn(String text) {
		LogManager.Process("Process in controller: /nlap/admin/text/classify/text/en");
		
		String classifyResult = classifyService.svmClassifyEn(text);

		LogManager.Process("Process out controller: /nlap/admin/text/classify/text/en");
		
		return classifyResult;
	}
	
	@RequestMapping(value = "/file", method = { RequestMethod.GET, RequestMethod.POST })
	public String classifyByFile(@RequestParam(value = "files", required = true) MultipartFile[] files) {
		LogManager.Process("Process in controller: /nlap/admin/text/classify/file");

		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "分类成功");
		
		List<ClassifyMultiResult> results = classifyService.svmClassifyByFile(files);
		map.put("results", results);
		
		LogManager.Process("Process out controller: /nlap/admin/text/classify/file");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	@RequestMapping(value = "/rule", method = { RequestMethod.GET, RequestMethod.POST })
	public String categorizateByFile(String templateId, 
			@RequestParam(value = "files", required = true) MultipartFile[] files) {
		LogManager.Process("Process in controller: /nlap/admin/text/classify/rule");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "分类成功");
		List<RuleClassifyResult> results = classifyService.ruleCalssify(templateId, files);
		map.put("results", results);

		LogManager.Process("Process out controller: /nlap/admin/text/classify/rule");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	@RequestMapping(value = "/classes", method = { RequestMethod.GET, RequestMethod.POST })
	public String getAllSvmClasses(String modelName) {
		LogManager.Process("Process in controller: /nlap/admin/text/classify/classes");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "获取所有分类类别成功");
		List<String> classes = classifyService.getAllSvmClasses(modelName);
		map.put("classes", classes);

		LogManager.Process("Process out controller: /nlap/admin/text/classify/classes");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	@RequestMapping(value = "/classes/en", method = { RequestMethod.GET, RequestMethod.POST })
	public String getAllSvmClassesEn(String modelName) {
		LogManager.Process("Process in controller: /nlap/admin/text/classify/classes/en");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "获取所有分类类别成功");
		Set<String> classes = classifyService.getAllSvmClassesEn(modelName);
		map.put("classes", classes);

		LogManager.Process("Process out controller: /nlap/admin/text/classify/classes/en");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	@RequestMapping(value = "/rules", method = { RequestMethod.GET, RequestMethod.POST })
	public String getAllRules() {
		LogManager.Process("Process in controller: /nlap/admin/text/classify/rules");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "获取文本分类下的模板名称列表成功");
		List<Object> rules = classifyService.getAllRules();
		map.put("rules", rules);

		LogManager.Process("Process out controller: /nlap/admin/text/classify/rules");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

}
