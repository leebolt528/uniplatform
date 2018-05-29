package com.bonc.uni.nlp.dao.task;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.task.Task;


/**
 * @ClassName:TaskRepository
 * @Package:com.bonc.text.repository.task
 * @Description:TODO
 * @author:xmy
 */
public interface TaskRepository extends JpaRepository<Task, String>{

	Task findOneByName(String name);

	List<Task> findAllByIdIn(String[] ids);

	@Query("select t from Task t where t.name like ?1")
	List<Task> findAllByNameLike(String searchWord, Pageable pageable);

	List<Task> findAllByBusiness(String businessId);

	List<Task> findAllByPlugin(String oldPluginId);

	List<Task> findAllByDataSource(String oldDataSourceId);

}
