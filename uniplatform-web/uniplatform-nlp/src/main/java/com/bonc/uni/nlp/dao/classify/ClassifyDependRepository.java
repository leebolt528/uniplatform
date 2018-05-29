package com.bonc.uni.nlp.dao.classify;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.classify.ClassifyDependence;

public interface ClassifyDependRepository extends JpaRepository<ClassifyDependence, String> {

	void deleteByPreClassifyId(String preClassifyId);
	
	void deleteByPostClassifyId(String postClassifyId);
	
	void deleteByPreObjectId(String preObjectId);
	
	List<ClassifyDependence> findAllByPreObjectId(String preObjectId);
	
	List<ClassifyDependence> findAllByPreClassifyIdAndPreObjectId(String preClassifyId, String preObjectId);
	
	List<ClassifyDependence> findAllByPreClassifyId(String preClassifyId);
	
	List<ClassifyDependence> findAllByPostClassifyId(String postClassifyId);
}
