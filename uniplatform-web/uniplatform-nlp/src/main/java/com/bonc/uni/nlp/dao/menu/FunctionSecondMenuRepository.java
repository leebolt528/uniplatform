package com.bonc.uni.nlp.dao.menu;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.menu.FunctionSecondMenu;

/** 
* @author : GaoQiuyuer 
* @version: 2018年1月5日 下午5:14:13 
*/
public interface FunctionSecondMenuRepository extends JpaRepository<FunctionSecondMenu, String>{

	@Query("select f from FunctionSecondMenu f where f.functionId = ?1 ORDER BY index ASC")
	List<FunctionSecondMenu> findAllByFunctionId(String functionId);

}
 