package com.bonc.uni.nlp.dao.label;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.label.LabelWordRelation;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月5日 下午2:53:24 
*/
public interface LabelWordRelationRepository extends JpaRepository<LabelWordRelation, String>{

	List<LabelWordRelation> findAllByLabelId(String labelId);

	@Query("select l from LabelWordRelation l where l.wordId in ?1")
	List<LabelWordRelation> findAllByWordIdIn(List<String> wordIds);
	
	List<LabelWordRelation> findAllByWordId(String id);

	LabelWordRelation findOneByLabelIdAndWordId(String id, String wordId);

	List<LabelWordRelation> findAllByDicId(String dicId);

	List<LabelWordRelation> findAllByDicIdAndLabelId(String dicId, String labelId);

}
 