package com.bonc.uni.nlp.dao.rule;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.rule.Template;

public interface TemplateRepository extends JpaRepository<Template, String>{

	Template findOneById(String templateId);
	
	Template findOneByName(String templateName);
	
	Template findOneByNameAndRuleTypeId(String template, String ruleTypeId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update Template t set t.name = ?1, t.userId = ?2 where t.id = ?3")
	int updateTemplate(String templateName, String userId, String templateId);
	
	List<Template> findAllByRuleTypeId(String ruleTypeId);
	
	@Query("select t from Template t where t.name like ?1 AND t.ruleTypeId IN ?2")
	List<Template> findAllByNameLike(String keyWord, List<String> ruleTypeIds);
	
	@Query("select count(t) from Template t where t.name like ?1 AND t.ruleTypeId IN ?2")
	int countByNameLike(String keyWord, List<String> ruleTypeIds);
}
