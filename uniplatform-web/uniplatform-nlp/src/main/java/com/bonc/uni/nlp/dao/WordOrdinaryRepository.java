package com.bonc.uni.nlp.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.dic.WordOrdinary;

public interface WordOrdinaryRepository extends JpaRepository<WordOrdinary, String>{

	@Query("select wo from WordOrdinary wo where wo.dictionaryId = ?1 and wo.word like ?2")
	List<WordOrdinary> findAllByNameLike(String dicId, String searchWord, Pageable pageable);
	
	@Query("select wo from WordOrdinary wo where  wo.word like ?1")
	List<WordOrdinary> findAllByNameLike( String searchWord, Pageable pageable);
	
	List<WordOrdinary> findOneByDictionaryId(String dicId);

	int countByDictionaryId(String dicId);

	List<WordOrdinary> findAllByDictionaryId(String id);

	@Query("select count(*) from WordOrdinary w where w.dictionaryId = ?1 and w.word like ?2")
	int countByDictionaryId(String dicId, String searchWord);

	WordOrdinary findAllByDictionaryIdAndWordAndNatureAndFrequency(String dicId, String word, String nature,
			int frequency);
	
	List<WordOrdinary> findAllByDictionaryId(String dicId, Pageable pageable);

	WordOrdinary findAllByDictionaryIdAndWord(String dicId, String word);

	@Query("select w from WordOrdinary w where w.id in ?1")
	List<WordOrdinary> findAllByIdsIn(List<String> wordIds, Pageable pageable);

	@Query("select count(*) from WordOrdinary w where w.id in ?1")
	int countByIdsIn(List<String> wordIds);

	@Query("select w from WordOrdinary w where w.id in ?1 and w.word like ?2")
	List<WordOrdinary> findAllByNameLike(List<String> wordIds, String keyword, Pageable pageable);

	@Query("select count(*) from WordOrdinary w where w.id in ?1 and w.word like ?2")
	int countByNameLike(List<String> wordIds, String keyword);

	@Query("select w from WordOrdinary w where w.dictionaryId = ?1 and w.id in ?2")
	List<WordOrdinary> findAllByDicIdAndWordIdIn(String dicId, List<String> wordIds);

	@Query("select w from WordOrdinary w where w.word like ?1")
	List<WordOrdinary> findAllByName(String keyWord);

	@Query("select w from WordOrdinary w where w.word = ?1 and dictionaryId = ?2")
	WordOrdinary findByNameAndDicId(String string, String id);

	@Query("select w from WordOrdinary w where w.dictionaryId = ?1 and  w.word = ?2 and nature = ?3 and w.frequency = ?4")
	WordOrdinary findByDictionaryIdAnd(String dictId, String string, String string2, int parseInt);

}
