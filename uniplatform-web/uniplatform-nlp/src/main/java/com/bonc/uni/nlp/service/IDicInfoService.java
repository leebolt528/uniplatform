package com.bonc.uni.nlp.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.entity.dic.DicType;
import com.bonc.uni.nlp.entity.dic.Dictionary;
import com.bonc.uni.nlp.entity.dic.WordMap;
import com.bonc.uni.nlp.entity.dic.WordSentiment;

/**
 * @author : GaoQiuyuer
 * @version: 2017年10月26日 下午3:12:25
 */
public interface IDicInfoService {
	
	Dictionary getDicFormat(String dicId);

	List<WordMap> getMapDicInfo(String dicId, String searchWord, int pageNumber, int pageSize);

	List<Object> getOrdinaryDicInfo(String dicId, String searchWord, int pageNumber, int pageSize);

	boolean addMapWord(String dicId, String wordKey, String wordValue);

	boolean addOrdWord(String dicId, String word, String wordLabel, String nature, int frequency);

	boolean deleteWord(String dicId, List<String> ids);

	boolean editMapWord(String dicId, String wordId, String newKey,  String newValue);

	boolean editOrdinaryWord(String dicId, String wordId, String newWord, String wordLabel, String newNature, int newFrequency);

	int getMapWordNumbers(String dicId, String searchWord);

	int getOrdinaryWordNumbers(String dicId, String searchWord);

	List<Integer> uploadWords2Dict(String dictId, MultipartFile[] file);

	boolean stopWords(String dicId, List<String> ids);

	boolean startWords(String dicId, List<String> ids);

	List<DicType> getAllDicType();

	/**
	 * 获取所有情感词库中的情感词
	 * 
	 * @return
	 */
	List<WordSentiment> getSentimentDicInfo(String dicId, String searchWord, int pageIndex, int pageSize);

	/**
	 * 情感词库添加情感词
	 * 
	 * @return
	 */
	boolean addSentimentWord(String dicId, String word, String nature, Double  grade);
	/**
	 * 情感词库修改情感词
	 * 
	 * @return
	 */
	boolean editSentimentWord(String dicId, String wordId, String newWord, String newNature, Double grade);

	boolean labelWord(String wordId, String labelIds);

	List<Map<String, String>> labelsInfo(String wordId);

	int getSentimentWordNumbers(String dicId, String searchWord);
}
