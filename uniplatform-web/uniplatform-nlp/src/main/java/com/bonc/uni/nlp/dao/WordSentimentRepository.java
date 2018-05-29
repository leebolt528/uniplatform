package com.bonc.uni.nlp.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.dic.WordSentiment;

public interface WordSentimentRepository extends JpaRepository<WordSentiment, String> {

	List<WordSentiment> findAllByDictionaryId(String dicId, Pageable pageable);

	List<WordSentiment> findAllByDictionaryIdAndWord(String dicId, String word);

	List<WordSentiment> findAllByDictionaryId(String dictId);

	@Query("select wo from WordSentiment wo where wo.dictionaryId = ?1 and wo.word like ?2")
	List<WordSentiment> findAllByNameLike(String dicId, String searchWord, Pageable pageable);

	@Query("select wo from WordSentiment wo where  wo.word like ?1")
	List<WordSentiment> findAllByNameLike(String searchWord, Pageable pageable);

	@Query("select wo from WordSentiment wo where  wo.word like ?1")
	List<WordSentiment> findAllByName(String keyWord);

	WordSentiment findOneByWord(String newWord);

	@Query("select wo from WordSentiment wo where wo.dictionaryId = ?1 and wo.word = ?2 and wo.nature = ?3 and wo.grade = ?4")
	WordSentiment findByDictionaryIdAnd(String dictId, String string, String string2, double parseDouble);
	
	@Query("select wo from WordSentiment wo where wo.dictionaryId = ?1 and wo.word = ?2 and wo.nature = ?3")
	WordSentiment findByDictionaryIds(String dictId, String string, String string2);

	@Query("select count(*) from WordSentiment w where w.dictionaryId = ?1 and w.word like ?2")
	int countByDictionaryId(String dicId, String searchWord);

}
