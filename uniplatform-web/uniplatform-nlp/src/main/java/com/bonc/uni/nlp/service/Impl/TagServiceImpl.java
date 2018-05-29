package com.bonc.uni.nlp.service.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bonc.uni.nlp.constant.DicStatusConstant;
import com.bonc.uni.nlp.dao.DicFuncRelationRepository;
import com.bonc.uni.nlp.dao.DicSubTypeForTypeRepository;
import com.bonc.uni.nlp.dao.DicSubTypeFunctionRelationRepository;
import com.bonc.uni.nlp.dao.DicSubTypeRepository;
import com.bonc.uni.nlp.dao.DicTypeRepository;
import com.bonc.uni.nlp.dao.DictionaryRepository;
import com.bonc.uni.nlp.dao.FuncitonRepository;
import com.bonc.uni.nlp.entity.dic.DicFuncRelation;
import com.bonc.uni.nlp.entity.dic.DicSubType;
import com.bonc.uni.nlp.entity.dic.DicType;
import com.bonc.uni.nlp.entity.dic.Dictionary;
import com.bonc.uni.nlp.entity.dic.Function;
import com.bonc.uni.nlp.service.ITagService;
import com.bonc.uni.nlp.utils.redis.publisher.DicPublisher;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年10月25日 下午4:29:51
 */
@Service
public class TagServiceImpl implements ITagService {

	@Autowired
	DicTypeRepository dicTypeRepository;
	@Autowired
	DictionaryRepository dicResourceRepository;
	@Autowired
	FuncitonRepository funcitonRepository;
	@Autowired
	DicSubTypeFunctionRelationRepository dicSubTypeFunctionRelationRepository;
	@Autowired
	DicFuncRelationRepository dicFuncRelationRepository;
	@Autowired
	DicSubTypeRepository dicSubTypeRepository;
	@Autowired
	FuncitonRepository functionRepository;
	@Autowired
	DicStatusServiceImpl dicStatusServiceImpl;
	@Autowired
	DicSubTypeForTypeRepository dicSubTypeForTypeRepository;
	@Autowired
	DictionaryRepository dictRepository;
	Sort sort = new Sort(Sort.Direction.ASC, "index");

	@Override
	public List<DicType> getTagsList() {
		List<DicType> dicTypes = new ArrayList<>();
		dicTypes = dicTypeRepository.findAll();
		return dicTypes;
	}

	@Override
	public int getDicNumbers(String typeId) {
		int dicNumbers = dicResourceRepository.countDic(typeId);
		return dicNumbers;
	}

	@Override
	public List<String> getFunctionIds(String typeId) {

		List<String> subTypIds = dicSubTypeForTypeRepository.findSubTypeIdByTypeIds(typeId);

		List<DicSubType> subTypeList = dicSubTypeRepository.findAll(subTypIds);
		Comparator<DicSubType> subTypeComparator = new Comparator<DicSubType>() {
			public int compare(DicSubType s1, DicSubType s2) {

				if (s1.getIndex() > s2.getIndex()) {
					return -1;
				}
				return 1;
			}
		};
		Collections.sort(subTypeList, subTypeComparator);
		List<String> functionsIds = new ArrayList<>();
		for (DicSubType dicSubType : subTypeList) {
			String dicSubTypeId = dicSubType.getId();

			List<String> findFunctionIds = dicSubTypeFunctionRelationRepository.findFunctionIds(dicSubTypeId);

			findFunctionIds.removeAll(functionsIds);

			functionsIds.addAll(findFunctionIds);

		}
		return functionsIds;
	}

	@Override
	public String getFunction(String functionId) {
		String funtion = funcitonRepository.findAllById(functionId);
		return funtion;
	}

	/**
	 * 根据词典与功能状态获取功能id
	 * 
	 * @param dicId
	 *            词典ID
	 * @param status
	 *            功能启用状态
	 * @return
	 */
	private List<String> getFunctionByDicAndStatus(String dicId, int status) {
		List<String> rsList = new ArrayList<>();
		List<DicFuncRelation> relationList = dicFuncRelationRepository.findAllByDicIdAndStatus(dicId, status);
		if (relationList.isEmpty()) {
			return rsList;
		}
		for (DicFuncRelation dicFuncRelation : relationList) {
			rsList.add(dicFuncRelation.getFunctionId());
		}
		return rsList;
	}

	@Override
	public List<Map<String, String>> getEnabledFunction(String dicId) {
		List<Map<String, String>> rsList = new ArrayList<>();
		List<String> enabledFunctions = getFunctionByDicAndStatus(dicId, 1);
		for (String functionId : enabledFunctions) {
			Map<String, String> tempMap = new HashMap<>();
			tempMap.put("functionId", functionId);
			tempMap.put("name", funcitonRepository.findAllById(functionId));
			rsList.add(tempMap);
		}
		return rsList;
	}

	@Override
	public List<Map<String, String>> getDisabledFunction(String dicId) {
		List<Map<String, String>> rsList = new ArrayList<>();
		List<String> enabledFunctions = getFunctionByDicAndStatus(dicId, 0);
		for (String functionId : enabledFunctions) {
			Map<String, String> tempMap = new HashMap<>();
			tempMap.put("functionId", functionId);
			tempMap.put("name", funcitonRepository.findAllById(functionId));
			rsList.add(tempMap);
		}
		return rsList;
	}

	@Override
	@Transactional
	public void enableDic(String dicId, String[] selectedFunctions) {
		List<String> enabledFunctions = getFunctionByDicAndStatus(dicId, 1);
		List<String> functions = new ArrayList<>();
		for (String functionId : selectedFunctions) {
			functions.add(functionId);
		}
		List<String> updateToEnabled = new ArrayList<>(functions);
		List<String> updateToDisabled = new ArrayList<>(enabledFunctions);
		updateToEnabled.removeAll(enabledFunctions);
		updateToDisabled.removeAll(functions);
		try {
			DicPublisher dicPublisher = new DicPublisher();
			for (String functionId : updateToEnabled) {
				dicFuncRelationRepository.updateDicStatus(1, dicId, functionId);
				dicPublisher.publishDic(dicId, functionId, true); // 向redis发布词库启用消息
			}
			for (String functionId : updateToDisabled) {
				dicFuncRelationRepository.updateDicStatus(0, dicId, functionId);
				dicPublisher.publishDic(dicId, functionId, false); // 向redis发布词库停用消息
			}
			Dictionary dic = dicResourceRepository.findOne(dicId);
			Integer status = dic.getStatus();
			List<DicFuncRelation> DicFuncRelations = dicFuncRelationRepository.findAllByDicIdAndStatus(dicId, 1);
			if (DicFuncRelations == null || DicFuncRelations.isEmpty()) {
				boolean sta = dicStatusServiceImpl.dicIfStartExceptbyName(DicStatusConstant.FUNCTION_DIC, dicId);
				if (sta) {
					List<DicFuncRelation> dicFuncRelation = dicFuncRelationRepository.findAllByDicId(dicId);
					if (dicFuncRelation == null || dicFuncRelation.isEmpty()) {

						dic.setStatus(0);
						dictRepository.save(dic);
					} else {

						dic.setStatus(1);
						dictRepository.save(dic);
					}
				} else {
					if (status < 2) {
						dic.setStatus(2);
						dicResourceRepository.save(dic);
					}

				}
			} else {
				if (status < 2) {
					dic.setStatus(2);
					dicResourceRepository.save(dic);
				}
			}

		} catch (Exception e) {
			LogManager.Exception(e);
		}
	}

	@Override
	public List<Map<String, Object>> getSubTypes(String typeId) {

		List<String> subTypIds = dicSubTypeForTypeRepository.findSubTypeIdByTypeIds(typeId);

		List<DicSubType> subTypeAll = dicSubTypeRepository.findAll(subTypIds);
		Comparator<DicSubType> subTypeComparator = new Comparator<DicSubType>() {
			public int compare(DicSubType s1, DicSubType s2) {

				if (s1.getIndex() > s2.getIndex()) {
					return -1;
				}
				return 1;
			}
		};
		Collections.sort(subTypeAll, subTypeComparator);

		List<Map<String, Object>> ordinarySubTypeAll = new ArrayList<>();
		// Map<String, Object> subTypes = getSubTypes();
		// ordinarySubTypeAll.add(subTypes);
		Function fun = new Function();
		fun.setDisplayName("全部");
		fun.setIndex(0);
		List<Function> functionAll = new ArrayList<>();
		for (DicSubType dicSubType : subTypeAll) {
			Map<String, Object> subtypeToFun = new HashMap<>();
			String id = dicSubType.getId();
			List<String> findFunctionIds = dicSubTypeFunctionRelationRepository.findFunctionIds(id);
		
			List<Function> function = functionRepository.findAll(findFunctionIds);
			function.add(fun);
			Comparator<Function> comparator = new Comparator<Function>() {
				public int compare(Function s1, Function s2) {
					if (s1.getIndex() > s2.getIndex()) {
						return 1;
					}
					return -1;
				}
			};

			Collections.sort(function, comparator);
			subtypeToFun.put("subType", dicSubType);
			subtypeToFun.put("function", function);
			ordinarySubTypeAll.add(subtypeToFun);
			List<Function> list = new ArrayList<>(function);
			list.removeAll(functionAll);
			if (!list.isEmpty()) {
				functionAll.addAll(list);
			}
		}

		DicSubType dic = new DicSubType();
		dic.setDisPlayName("全部");

		Map<String, Object> subtypeToFun = new HashMap<>();
		subtypeToFun.put("subType", dic);
		subtypeToFun.put("function", functionAll);
		ordinarySubTypeAll.add(0, subtypeToFun);

		return ordinarySubTypeAll;

	}

	@Override
	public Map<String, Object> getSubTypes() {

		DicSubType dic = new DicSubType();
		dic.setDisPlayName("全部");
		Function fun = new Function();
		fun.setDisplayName("全部");

		List<Function> functions = functionRepository.findAll(sort);
		functions.add(0, fun);

		Map<String, Object> subtypeToFun = new HashMap<>();
		subtypeToFun.put("subType", dic);
		subtypeToFun.put("function", functions);

		return subtypeToFun;

	}

	/**
	 * 停用所有标签
	 */
	@Override
	@Transactional
	public void disableAllFunction(String dicId) {
		dicFuncRelationRepository.updateDicStatus(0, dicId);
		List<DicFuncRelation> dicFuncRelation = dicFuncRelationRepository.findAllByDicId(dicId);
		if(!dicFuncRelation.isEmpty()) {
			// 向redis发布消息停用这些功能
			DicPublisher publisher = new DicPublisher();
			for(DicFuncRelation relation : dicFuncRelation) {
				publisher.publishDic(dicId, relation.getFunctionId(), false);
			}
		}
		
		boolean sta = dicStatusServiceImpl.dicIfStartExceptbyName(DicStatusConstant.FUNCTION_DIC, dicId);
		if (sta) {
			if (dicFuncRelation == null || dicFuncRelation.isEmpty()) {
				Dictionary dictionary = dictRepository.findOne(dicId);
				dictionary.setStatus(0);
				dictRepository.save(dictionary);
			} else {
				Dictionary dictionary = dictRepository.findOne(dicId);
				dictionary.setStatus(1);
				dictRepository.save(dictionary);
				
			}
		} else {
			Dictionary dic = dicResourceRepository.findOne(dicId);
			Integer status = dic.getStatus();
			if (status < 2) {
				dic.setStatus(2);
				dicResourceRepository.save(dic);
			}
		}
	}

}
