package com.bonc.uni.nlp.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.uni.nlp.constant.DicStatusConstant;
import com.bonc.uni.nlp.dao.DicFuncRelationRepository;
import com.bonc.uni.nlp.dao.strategy.StrategyDicRelationRepository;
import com.bonc.uni.nlp.entity.dic.DicFuncRelation;
import com.bonc.uni.nlp.entity.strategy.StrategyDicRelation;
import com.bonc.uni.nlp.service.IDicStatusService;

@Service
public class DicStatusServiceImpl implements IDicStatusService {
	@Autowired
	DicFuncRelationRepository dicFuncRelationRepository;
	@Autowired
	StrategyDicRelationRepository strategyDicRelationRepository;

	/**
	 * 判断除了已传入别名外，其他功能是否使用词典
	 * 
	 * @param str
	 *            别名 （DicStatusConstant）；
	 * @param dicId
	 * @return true 无应用   false 有应用
	 */
	@Override
	public boolean dicIfStartExceptbyName(String str, String dicId) {

		if (DicStatusConstant.ALL_DIC.equals(str)) {
			boolean strategyDic = strategyIfStartDic(dicId);
			boolean functionDic = functionIfStartDic(dicId);
			if (strategyDic && functionDic) {
				return true;
			} else {
				return false;
			}
		}
		if (DicStatusConstant.FUNCTION_DIC.equals(str)) {
			boolean strategyDic = strategyIfStartDic(dicId);

			if (strategyDic) {
				return true;
			} else {
				return false;
			}
		}

		if (DicStatusConstant.STRATEGY_DIC.equals(str)) {
			boolean functionDic = functionIfStartDic(dicId);

			if (functionDic) {
				return true;
			} else {
				return false;
			}
		}
		return false;

	}

	/**
	 * 判断词库标签是否(候选状态)存在
	 * 
	 * @param dicId
	 * @return true 无应用 false 有应用
	 */
	@Override
	public boolean functionIfCandidateDic(String dicId) {
		List<DicFuncRelation> dicFuncRelation = dicFuncRelationRepository.findAllByDicId(dicId);
		if (dicFuncRelation == null || dicFuncRelation.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断词库标签是否在启用
	 * 
	 * @param dicId
	 * @return true 无应用 false 有应用
	 */
	@Override
	public boolean functionIfStartDic(String dicId) {
		List<DicFuncRelation> dicFuncRelation = dicFuncRelationRepository.findAllByDicIdAndStatus(dicId, 1);
		if (dicFuncRelation == null || dicFuncRelation.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断词库策略是否在启用
	 * 
	 * @param dicId
	 * @return true 无应用 false 有应用
	 */
	@Override
	public boolean strategyIfStartDic(String dicId) {
		List<StrategyDicRelation> strategyDicRelations = strategyDicRelationRepository.findAllByDicId(dicId);
		if (strategyDicRelations == null || strategyDicRelations.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

}
