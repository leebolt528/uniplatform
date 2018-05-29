package com.bonc.uni.nlp.service.Impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bonc.uni.nlp.dao.DicFuncRelationRepository;
import com.bonc.uni.nlp.dao.DicSubTypeFunctionRelationRepository;
import com.bonc.uni.nlp.dao.DictionaryRepository;
import com.bonc.uni.nlp.dao.FuncitonRepository;
import com.bonc.uni.nlp.entity.dic.DicFuncRelation;
import com.bonc.uni.nlp.entity.dic.Dictionary;
import com.bonc.uni.nlp.entity.dic.Function;
import com.bonc.uni.nlp.service.IDicEditService;
import com.bonc.usdp.odk.common.string.StringUtil;

/**
 * @author : GaoQiuyuer
 * @version: 2017年10月30日 下午6:40:33
 */
@Service
@Transactional
public class DicEditServiceImpl implements IDicEditService {

	@Autowired
	DicFuncRelationRepository dicFuncRelationRepository;
	@Autowired
	DictionaryRepository dictRepository;
	@Autowired
	FuncitonRepository funcitonRepository;
	@Autowired
	DicSubTypeFunctionRelationRepository dicSubTypeFunctionRelationRepository;
	@Autowired
	DicStatusServiceImpl dicStatusServiceImpl;
	Sort sort = new Sort(Sort.Direction.DESC, "createTime");

	@Override
	public List<DicFuncRelation> getTaggedCurrent(String dicId) {

		List<DicFuncRelation> tagsList = dicFuncRelationRepository.findAllByDicId(dicId);
		return tagsList;
	}

	@Override
	public int updateDictionaryInfo(String dictId, String newDictName) {
		Dictionary dic = dictRepository.findOne(dictId);
		String name = dic.getName();
		if (newDictName.equals(name)) {
			return 1;
		}
		Dictionary existedDictByName = dictRepository.findByName(newDictName);
		if (existedDictByName != null) {
			return 0;
		}

		if (null == dic)
			return 0;
		dic.setName(newDictName);
		dictRepository.save(dic);
		return 1;
	}

	@Override
	public int tagged(String dicId, String tagIds) {
		Dictionary findOne4 = dictRepository.findOne(dicId);
		Dictionary findOne3 = findOne4;
		Dictionary findOne2 = findOne3;
		Dictionary findOne = findOne2;
		Dictionary existedDict = findOne;
		if (null == existedDict)
			return 0;

		List<DicFuncRelation> tagsList = getTaggedCurrent(dicId);
		// 数据库已有标签id
		List<String> taggedCurrentList = new ArrayList<>();
		if (tagsList != null && tagsList.size() != 0) {
			for (DicFuncRelation dicFuncRelation : tagsList) {
				taggedCurrentList.add(dicFuncRelation.getFunctionId());
			}

		}
		// 新添加的标签id
		List<String> tagIdsList = new ArrayList<>();
		if (!StringUtil.isEmpty(tagIds)) {
			String[] arrTagIds = tagIds.split(",");
			for (String tagId : arrTagIds) {
				tagIdsList.add(tagId);
			}
		}
		// 数据库要删除的标签id
		List<String> diffDel = new ArrayList<String>();
		if (!tagIdsList.isEmpty()) {
			for (String str : taggedCurrentList) {
				if (!tagIdsList.contains(str)) {
					diffDel.add(str);
				}
			}
		} else {
			for (String str : taggedCurrentList) {

				diffDel.add(str);

			}
		}
		// 数据库要添加的标签id
		List<String> diffAdd = new ArrayList<String>();
		if (!tagIdsList.isEmpty()) {

			for (String str : tagIdsList) {
				if (!taggedCurrentList.contains(str)) {
					diffAdd.add(str);
				}
			}
		}

		if (!diffDel.isEmpty()) {

			for (int i = 0; i < diffDel.size(); i++) {
				dicFuncRelationRepository.deleteByDicIdAndFunctionId(dicId, diffDel.get(i));
			}
		}

		if (!diffAdd.isEmpty()) {

			List<DicFuncRelation> dicFuncRelations = new ArrayList<>();
			for (String tagId : diffAdd) {
				DicFuncRelation dicFuncRelation = new DicFuncRelation();
				dicFuncRelation.setDicId(dicId);
				dicFuncRelation.setFunctionId(tagId);
				dicFuncRelation.setStatus(0);
				dicFuncRelations.add(dicFuncRelation);
			}
			dicFuncRelationRepository.save(dicFuncRelations);
		}
		List<DicFuncRelation> dicFuncRelation = dicFuncRelationRepository.findAllByDicId(dicId);

		if (dicFuncRelation == null || dicFuncRelation.isEmpty()) {
			Dictionary dictionary = dictRepository.findOne(dicId);
			dictionary.setStatus(0);
			dictRepository.save(dictionary);
		} else {
			Dictionary dictionary = dictRepository.findOne(dicId);
			dictionary.setStatus(1);
			dictRepository.save(dictionary);
		}
		return 1;
	}

	@Override
	public List<String> getFunctionId(String dicId) {

		List<DicFuncRelation> tagsList = dicFuncRelationRepository.findAllByDicId(dicId);
		if (tagsList == null || tagsList.size() == 0) {
			return null;
		}
		List<String> functionIds = new ArrayList<>();

		for (DicFuncRelation dicFuncRelation : tagsList) {
			functionIds.add(dicFuncRelation.getFunctionId());
		}

		return functionIds;
	}

	@Override
	public List<Function> getFunction(List<String> dicIds) {
		return funcitonRepository.findAll(dicIds);
	}

	@Override
	public List<Function> getFunctionAll(String dicId, List<String> functionIds) {
		Dictionary dictionary = dictRepository.findOne(dicId);
		String dicSubTypeId = dictionary.getDicSubTypeId();
		if (functionIds == null || functionIds.isEmpty()) {
			List<String> functionIdAll = dicSubTypeFunctionRelationRepository.findFunctionIds(dicSubTypeId);
			List<Function> functionAll = funcitonRepository.findAll(functionIdAll);

			return functionAll;
		}

		List<String> functionIdAll = dicSubTypeFunctionRelationRepository.findFunctionIds(dicSubTypeId);
		functionIdAll.removeAll(functionIds);
		List<Function> functionAll = funcitonRepository.findAll(functionIdAll);

		return functionAll;
	}

	@Override
	public int rmFunctiondAll(String dicId) {
		List<DicFuncRelation> tagsList =new ArrayList<>();
		tagsList = dicFuncRelationRepository.findAllByDicId(dicId);
		if(!tagsList.isEmpty()){
			dicFuncRelationRepository.delete(tagsList);
		}
		Dictionary dictionary = dictRepository.findOne(dicId);
		dictionary.setStatus(0);
		dictRepository.save(dictionary);
		return 0;
	}
}
