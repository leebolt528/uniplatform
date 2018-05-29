package com.bonc.uni.nlp.service.Impl.corpus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bonc.uni.nlp.dao.FuncitonRepository;
import com.bonc.uni.nlp.dao.corpus.CorpusTypeRepository;
import com.bonc.uni.nlp.entity.corpus.CorpusType;
import com.bonc.uni.nlp.entity.dic.Function;
import com.bonc.uni.nlp.service.corpus.ICorpusTypeService;

@Service
@Transactional
public class CorpusTypeServiceImpl implements ICorpusTypeService {

	@Autowired
	CorpusTypeRepository corpusTypeRepository;

	@Autowired
	FuncitonRepository functionRepository;
	
	public Sort sortByIndex = new Sort(Sort.Direction.ASC, "index");

	public static final String UNTREATED = "untreated";

	public static final String ALL = "all";

	@Override
	public Map<String, Object> getCorpusTypeAndFunction() {
		Map<String, Object> returnMap = new HashMap<>();
		List<CorpusType> corpusTypes = corpusTypeRepository.findAll(sortByIndex);
		
		for (CorpusType corpusType : corpusTypes) {
			if (!UNTREATED.equals(corpusType.getName()) && !ALL.equals(corpusType.getName())) {
				String corpusTypeId = corpusType.getId();
				Map<String, Object> tempCorpusType = new HashMap<>();
				tempCorpusType.put("corpusTypeName", corpusType.getDisplayName());
				List<Function> functions = functionRepository.findAllByCorpusTypeId(corpusTypeId, sortByIndex);
				List<Map<String, String>> functionsList = new ArrayList<>();
				for (Function function : functions) {
					Map<String, String> tempMap = new HashMap<>();
					tempMap.put("functionId", function.getId());
					tempMap.put("functionName", function.getDisplayName());
					functionsList.add(tempMap);
				}
				tempCorpusType.put("functions", functionsList);
				returnMap.put(corpusTypeId, tempCorpusType);
			}
		}
		return returnMap;
	}

}
