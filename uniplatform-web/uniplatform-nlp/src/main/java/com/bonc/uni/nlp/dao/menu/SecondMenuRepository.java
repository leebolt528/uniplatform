package com.bonc.uni.nlp.dao.menu;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.menu.SecondMenu;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月30日 下午7:54:28 
*/
public interface SecondMenuRepository extends JpaRepository<SecondMenu, String>{

	/**
	 * @param firstMenuId
	 * @return
	 */
	@Query("select s from SecondMenu s where s.firstMenuId = ?1 ORDER BY index ASC")
	List<SecondMenu> findAllByFirstMenuId(String firstMenuId);

}
 