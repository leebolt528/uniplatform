package com.bonc.uni.nlp.dao.menu;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.dic.FunctionDisplay;

/** 
* @author : GaoQiuyuer 
* @version: 2018年1月12日 上午11:36:33 
*/
public interface FunctionDisplayRepository extends JpaRepository<FunctionDisplay, String>{
	
	@Query("select f from FunctionDisplay f where f.hasDisplay = 1 ORDER BY index ASC")
	List<FunctionDisplay> findAllIndex();

}
 