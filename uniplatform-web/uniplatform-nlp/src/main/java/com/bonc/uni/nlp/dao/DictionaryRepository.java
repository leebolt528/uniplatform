package com.bonc.uni.nlp.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.dic.Dictionary;

/**
 * @author : GaoQiuyuer
 * @version: 2017年10月25日 下午6:49:39
 */
public interface DictionaryRepository extends JpaRepository<Dictionary, String> {

	@Query("select count(dr.name) from Dictionary dr where dr.dicTypeId = ?1")
	int countDic(String typeId);

	Dictionary findByUserIdAndNameAndDicSubTypeId(String usetId, String name, String dicSubTypeId);

	Dictionary findOneById(String dicId);

	List<Dictionary> findAllByDicTypeId(String dicTypeId,Sort sort);

	Dictionary findByName(String dictName);

	@Query("select dic from Dictionary dic where  dic.name like ?1")
	List<Dictionary> findAllByNameLike(String searchWord, Pageable pageable);

	int countByName(String name);

	List<Dictionary> findAllByDicSubTypeId(String subTypeId,Sort sort);

	List<Dictionary> findAllByDicTypeIdAndDicSubTypeId(String dicTypeId, String subTypeId,Sort sort);

	@Query("select dic.id from Dictionary dic where  dic.dicSubTypeId =?1")
	List<String> findDicIdBySubTypeId(String subTypeId,Sort sort);

	@Query("select dic.id from Dictionary dic where  dic.dicSubTypeId =?1 and dic.dicTypeId =?2")
	List<String> findDicIdBySubTypeIdAndTypeId(String subTypeId, String typeId,Sort sort);

	List<Dictionary> findAllById(String dicId,Sort sort);

	List<Dictionary> findAllByDicTypeIdAndDicSubTypeId(String dicTypeId, String dicSubTypeId);

	@Query("select d from Dictionary d where d.name like ?1")
	List<Dictionary> findAllByName(String keyWord);

	@Query("select d from Dictionary d where d.name like ?1 and d.dicTypeId = ?2")
	List<Dictionary> findAllByNameAndDicTypeId(String keyWord, String dicTypeId);

}
