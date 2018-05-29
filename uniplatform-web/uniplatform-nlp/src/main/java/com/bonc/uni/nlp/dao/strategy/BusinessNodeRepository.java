package com.bonc.uni.nlp.dao.strategy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.strategy.BusinessNode;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月10日 上午11:30:20 
*/
public interface BusinessNodeRepository extends JpaRepository<BusinessNode, String>{

	/**
	 * @param nodeName
	 * @return
	 */
	BusinessNode findOneByName(String nodeName);

	/**
	 * @return
	 */
	@Query("select b from BusinessNode b ORDER BY createTime DESC")
	List<BusinessNode> find();

}
 