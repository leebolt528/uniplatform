package com.bonc.uni.nlp.dao.label;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.label.Label;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月5日 下午2:51:51 
*/
public interface LabelRepository extends JpaRepository<Label, String>{

	Label findOneByName(String labelName);

	@Query("select l from Label l where l.name like ?1")
	List<Label> findAllByNameLike(String keyword, Pageable pageable);

	@Query("select count(*) from Label l where l.name like ?1")
	int count(String keyword);

}
 