package com.bonc.uni.nlp.service.Impl.product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.text.sdk.client.TextClassRuleClient;
import com.bonc.text.sdk.client.TextClassifySvmClient;
import com.bonc.text.sdk.client.en.TextClassModelEnglishClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.dao.rule.NodeRepository;
import com.bonc.uni.nlp.dao.rule.RuleRepository;
import com.bonc.uni.nlp.dao.rule.RuleTypeRepository;
import com.bonc.uni.nlp.dao.rule.TemplateRepository;
import com.bonc.uni.nlp.entity.product.ClassifyMultiResult;
import com.bonc.uni.nlp.entity.product.ClassifyResult;
import com.bonc.uni.nlp.entity.product.RuleClassifyResult;
import com.bonc.uni.nlp.entity.rule.RuleType;
import com.bonc.uni.nlp.entity.rule.Template;
import com.bonc.uni.nlp.service.product.ITextClassifyService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月23日 下午4:12:44
 */
@Service
public class TextClassifyServiceImpl implements ITextClassifyService {

	@Autowired
	RuleTypeRepository ruleTypeRepository;
	@Autowired
	TemplateRepository templateRepository;
	@Autowired
	NodeRepository nodeRepository;
	@Autowired
	RuleRepository ruleRepository;

	@Override
	public String svmClassify(String text) {
		TextClassifySvmClient client = TextClassifySvmClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

		return client.multiResultClassify(text);
	}

	@Override
	public String svmClassifyEn(String text) {
		TextClassModelEnglishClient client = TextClassModelEnglishClient.getInstance(SystemConfig.PROCESSOR_DOMAIN_EN);
		return client.modelClassifier(text);
	}
	
	@Override
	public Set<String> getAllSvmClassesEn(String modelName) {
		TextClassModelEnglishClient client = TextClassModelEnglishClient.getInstance(SystemConfig.PROCESSOR_DOMAIN_EN);
		return client.listModelClasses(modelName);
	}

	@Override
	public List<ClassifyMultiResult> svmClassifyByFile(MultipartFile[] files) {
		LogManager.method("Enter the method : svmClassifyByFile");

		List<ClassifyMultiResult> results = new ArrayList<>();
		if (null == files) {
			LogManager.warn("Null file.s");
			return results;
		}

		LogManager.Debug("Files num :" + files.length);

		TextClassifySvmClient client = TextClassifySvmClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

		for (MultipartFile file : files) {
			ClassifyMultiResult result = new ClassifyMultiResult();
			String fileName = file.getOriginalFilename();
			result.setFileName(fileName);
			try {
				String text = this.readMultipartFile2String(file);
				String jsonResult = client.multiResultClassify(text);
				JSONObject resultObj = JSON.parseObject(jsonResult);
				String status = resultObj.getString("status");
				if (!"200".equals(status)) {
					continue;
				}
				String classifyResult = resultObj.getString("result");
				List<ClassifyResult> classes = JSON.parseArray(classifyResult, ClassifyResult.class);
				result.setClasses(classes);

				results.add(result);
			} catch (Exception e) {
				continue;
			}

		}
		LogManager.method("Out from the method : svmClassifyByFile");
		return results;
	}

	@Override
	public List<RuleClassifyResult> ruleCalssify(String templateId, MultipartFile[] files) {
		TextClassRuleClient client = TextClassRuleClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);
		List<RuleClassifyResult> results = new ArrayList<>();
		for (MultipartFile file : files) {
			try {
				String fileName = file.getOriginalFilename();

				RuleClassifyResult result = new RuleClassifyResult();
				result.setFileName(fileName);

				String content = this.readMultipartFile2String(file);
				String resultJson = client.classifyMultiResult(templateId, content);

				String classesJson = JSON.parseObject(resultJson).getString("className");
				List<String> classNames = JSON.parseArray(classesJson, String.class);
				result.setClassNames(classNames);

				results.add(result);
			} catch (Exception e) {
				continue;
			}
		}
		return results;
	}

	private String readMultipartFile2String(MultipartFile file) {
		StringBuilder content = new StringBuilder();
		try (InputStream in = file.getInputStream();
				InputStreamReader inReader = new InputStreamReader(in);
				BufferedReader reader = new BufferedReader(inReader)) {
			String line = null;
			while (null != (line = reader.readLine())) {
				content.append(line);
			}
		} catch (IOException e) {
			LogManager.Exception(e);
		}
		return content.toString();
	}

	@Override
	public List<String> getAllSvmClasses(String modelName) {
		TextClassifySvmClient client = TextClassifySvmClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);
		return client.listModelClasses(modelName);
	}

	@Override
	public List<Object> getAllRules() {
		List<Object> templateInfos = new ArrayList<>();
		Map<String, String> templateMap = new HashMap<>();
		RuleType ruleType = ruleTypeRepository.findOneByName("classify");
		String ruleTypeId = ruleType.getId();
		List<Template> templates = templateRepository.findAllByRuleTypeId(ruleTypeId);
		for (Template template : templates) {
			templateMap = new HashMap<>();
			templateMap.put("templateId", template.getId());
			templateMap.put("templateName", template.getName());
			templateInfos.add(templateMap);
		}
		return templateInfos;
	}

}
