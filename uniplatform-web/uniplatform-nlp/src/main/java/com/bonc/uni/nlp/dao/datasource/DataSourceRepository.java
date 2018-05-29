package com.bonc.uni.nlp.dao.datasource;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bonc.uni.nlp.entity.datasource.DataSource;



/**
 * @ClassName:DataSourceRepository
 * @Package:com.bonc.text.repository.datasource
 * @Description:TODO
 * @author:xmy
 */
public interface DataSourceRepository extends JpaRepository<DataSource, String>{
	
	DataSource findOneByName(String name);
	
	List<DataSource> findAllByIdIn(String[] ids);
	
	void deleteByIdIn(String[] ids);

	@Query("select ds from DataSource ds where ds.name like :searchWord")
	List<DataSource> findAllByNameLike(@Param("searchWord") String searchWord,Pageable pageable);

	/**
	 * @return
	 */
	@Query("select d from DataSource d order by createTime DESC")
	List<DataSource> findAllByTime();
	
}
