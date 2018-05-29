package com.bonc.uni.nlp.dao.rule;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.rule.Rule;

public interface RuleRepository extends JpaRepository<Rule, String> {
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update Rule r set r.rule = ?1, r.userId = ?2 where r.id = ?3")
	int updateRule(String rule, String userId, String ruleId);

	Rule findOneByRule(String rule);
	
	Rule findOneByRuleAndNodeId(String rule, String nodeId);
	
	List<Rule> findAllByNodeId(String nodeId, Sort sort);

	@Query("select r from Rule r where r.rule like ?1 AND r.nodeId IN ?2")
	List<Rule> findAllByRuleLike(String keyWord, List<String> nodeId, Sort sortByCreateTime);
	
	@Query("select count(r) from Rule r where r.rule like ?1 AND r.nodeId IN ?2")
	int countByRuleLike(String keyWord, List<String> nodeId);
	/**
	 * @param algorithmId
	 * @return
	 */
	List<Rule> findAllByAlgorithmId(String algorithmId);
	/**
	 * 根据节点id删除规则
	 * @param nodeId
	 */
	void deleteByNodeId(String nodeId);
	/**
	 * @param detailContent
	 * @param id
	 * @return
	 */
	Rule findOneByRuleAndAlgorithmId(String detailContent, String id);

	/**
	 * @param algorithmId
	 * @return
	 */
	Rule findOneByAlgorithmId(String algorithmId);

	/**
	 * @param id
	 * @return
	 */
	List<Rule> findAllByNodeId(String id);
	/**
	 * 根据节点id计算总数
	 * @param nodeId
	 * @return
	 */
	int countByNodeId(String nodeId);
}
