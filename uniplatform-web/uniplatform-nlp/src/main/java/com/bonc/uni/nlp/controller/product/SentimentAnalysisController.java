package com.bonc.uni.nlp.controller.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.uni.nlp.entity.product.EmotionResult;
import com.bonc.uni.nlp.service.product.ISentimentAnalysisService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午5:47:36 
*/
@RestController
@CrossOrigin
@RequestMapping(value= "/nlap/admin/sentimentAnalysis")
public class SentimentAnalysisController {

	@Autowired
	ISentimentAnalysisService sentimentService;
	
	@RequestMapping(value="/emotion/ceils", method = { RequestMethod.GET, RequestMethod.POST })
	public String sentimentAnalysis(String text){
		LogManager.Process("Process in controller: /nlap/admin/sentimentAnalysis/emotion");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "获取情感单元成功");
		
		List<EmotionResult> results = sentimentService.getEmotionCeils(text);
		map.put("data", results);
		
		LogManager.Process("Process out controller: /nlap/admin/sentimentAnalysis/emotion");
		return JSON.toJSONString(map);
		
	}
	
	@RequestMapping(value="/analyse", method = { RequestMethod.GET, RequestMethod.POST })
	public String inclinationAanalysis(String text){
		LogManager.Process("Process in controller: /nlap/admin/sentimentAnalysis/analyse");
		String result = sentimentService.analysisEmotion(text);
		
		LogManager.Process("Process out controller: /nlap/admin/sentimentAnalysis/analyse");
		return result;
		
	}
}
 