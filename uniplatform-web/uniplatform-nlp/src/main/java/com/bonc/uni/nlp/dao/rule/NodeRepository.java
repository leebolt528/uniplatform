package com.bonc.uni.nlp.dao.rule;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.rule.Node;

public interface NodeRepository extends JpaRepository<Node, String> {

	Node findOneById(String nodeId);
	
//	Node findOneByName(String nodeName);
	
	Node findOneByNameAndTemplateId(String name, String templateId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update Node n set n.name = ?1, n.userId = ?2 where n.id = ?3")
	int updateNode(String nodeName, String userId, String nodeId);
	
	List<Node> findAllByTemplateId(String templateId, Sort sort);

	List<Node> findAllByTemplateId(String id);
}
