package com.bonc.uni.nlp.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.dic.WordMap;

public interface WordMapRepository extends JpaRepository<WordMap, String>{

	@Query("select wm from WordMap wm where wm.dictionaryId = ?1 and wm.wordKey like ?2")
	List<WordMap> findAllByNameLike(String dicId, String searchWord, Pageable pageable);
	
	@Query("select wm from WordMap wm where  wm.wordKey like ?1")
	List<WordMap> findAllByNameLike(String dicId,  Pageable pageable);

	List<WordMap> findOneByDictionaryId(String dicId);

	int countByDictionaryId(String dicId);

	List<WordMap> findAllByDictionaryId(String dicId);

	@Query("select count(*) from WordMap w where w.dictionaryId = ?1 and w.wordKey like ?2")
	int countByDictionaryId(String dicId, String searchWord);

	List<WordMap> findAllByDictionaryIdAndWordKey(String dicId, String wordKey);

	List<WordMap> findAllByDictionaryId(String dicId, Pageable pageable);

	List<WordMap> findAllByWordKey(String wordKey);

	void deleteAllByWordKey(String wordKey);

	@Query("select wm from WordMap wm where  wm.wordKey like ?1")
	List<WordMap> findAllByName(String keyWord);

	WordMap findOneByWordKey(String newKey);

	WordMap findOneByWordKeyAndDictionaryId(String newKey, String dictionaryId);

	@Query("select wm from WordMap wm where wm.dictionaryId = ?1 and wm.wordKey = ?2 and wm.wordValue = ?3")
	WordMap findByDictionaryIdAnd(String dictId, String wordKey, String wordValue);

	
}
