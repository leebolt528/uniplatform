package com.bonc.uni.nlp.dao.plugin;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bonc.uni.nlp.entity.plugin.Plugin;

/**
 * @ClassName:PluginRepository
 * @Package:com.bonc.text.repository.plugin
 * @Description:TODO
 * @author:xmy
 */
public interface PluginRepository extends JpaRepository<Plugin, String>{

	Plugin findOneByName(String name);

	List<Plugin> findAllByIdIn(String[] ids);

	@Query("select pl from Plugin pl where pl.name like :searchWord")
	List<Plugin> findAllByNameLike(@Param("searchWord") String searchWord, Pageable pageable);

	@Query("select p.name from Plugin p")
	List<String> findAllName();

	/**
	 * @return
	 */
	@Query("select p from Plugin p order by uploadTime DESC")
	List<Plugin> findAllByTime();

}
