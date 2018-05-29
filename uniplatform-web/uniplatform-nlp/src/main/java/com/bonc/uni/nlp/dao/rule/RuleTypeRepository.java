package com.bonc.uni.nlp.dao.rule;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.rule.RuleType;

public interface RuleTypeRepository extends JpaRepository<RuleType, String> {

	RuleType findOneById(String ruleTypeId);
	
	RuleType findOneByDisplayName(String displayName);

	RuleType findOneByName(String functionName);
}
