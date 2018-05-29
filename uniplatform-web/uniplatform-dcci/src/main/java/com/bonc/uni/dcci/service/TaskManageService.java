package com.bonc.uni.dcci.service;

import com.bonc.uni.dcci.entity.TaskAssign;
import com.bonc.uni.dcci.entity.TaskManage;
import com.bonc.uni.dcci.entity.TaskRelation;
import com.bonc.uni.dcci.entity.TaskSite;
import com.bonc.uni.dcci.util.CrawlerType;
import com.bonc.uni.dcci.util.LevelType;
import com.bonc.uni.dcci.util.StatusType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 任务管理服务
 * @author futao
 * 2017年9月11日
 */
public interface TaskManageService {

	/**
	 * 保存
	 * @param taskManage
	 * @return
	 */
	boolean save(TaskManage taskManage);
	
	TaskManage saveTaskManage(TaskManage taskManage);
	
	TaskAssign saveTaskAssign(TaskAssign taskAssign);
	
	/**
	 * 修改
	 * @param taskManage
	 * @return
	 */
	boolean update(TaskManage taskManage);
	
	/**
	 * 获取对象
	 * @param id
	 * @return
	 */
	TaskManage getById(Integer id);
	TaskManage getByRelation(int taskRelation);
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	boolean delById(Integer id);
	
	/**
	 * 列表
	 * @param name
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<TaskManage> list(String name,StatusType status,CrawlerType crawlerType,int pageNum, int pageSize);
	
	/**添加
	 * @param taskRelation
	 * @return
	 */
	TaskRelation saveTaskRelation(TaskRelation taskRelation);
	
	/**删除
	 * @param id
	 * @return
	 */
	boolean delTaskRelationById(Integer id);
	
	int delTaskSite(int taskRelation);
	
	int delTaskAssign(int taskManage);
	/**
	 * 撤回任务的site
	 * @param taskRelation
	 * @param userId
	 * @param status
	 * @return
	 */
	int updateSiteRecall(int taskRelation, int userId,StatusType.UrlSiteType status);
	
	/**添加
	 * @param taskSites
	 * @return
	 */
	List<TaskSite> savesTaskSite(List<TaskSite> taskSites);
	
	/**
	 * @param taskSites
	 * @return
	 */
	boolean updsTaskSite(List<TaskSite> taskSites);
	
	/**
	 * @param taskAssigns
	 * @return
	 */
	List<TaskAssign> savesTaskAssign(List<TaskAssign> taskAssigns);
	
	/**删除
	 * @param taskSites
	 * @return
	 */
	boolean delTaskSite(List<TaskSite> taskSites);
	
	/**列出所有
	 * @param TaskRelation
	 * @return
	 */
	List<TaskSite> listTaskSite(int taskRelation,String name,StatusType.UrlSiteType status);
	
	
	
	/**
	 * 翻页查询   查询全部 
	 * @param taskRelation
	 * @param userId
	 * @param name
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<TaskSite> listTaskSitePage(int taskRelation,String name,StatusType.UrlSiteType status,int pageNum, int pageSize);

	/**查看分配的任务  根据用户id查询
	 * @param userId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	 List<TaskAssign> listTaskAssignUser(int userId);
	
	/**查看分配的任务
	 * @param taskManage
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<TaskAssign> listTaskAssign(int taskManage,int pageNum, int pageSize);
	
	/**
	 * @param name
	 * @param status
	 * @param ids
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<TaskManage> list(String name,StatusType status,Object[] ids ,int pageNum, int pageSize);
	
	/**
	 * @param userId
	 * @param taskManage
	 * @return
	 */
	TaskAssign getTaskAssignUser(int userId,int taskManage);
	
	/**
	 * @param taskAssign
	 * @return
	 */
	boolean updTaskAssign(TaskAssign taskAssign);
	
	/**
	 * @param id
	 * @return
	 */
	TaskAssign getTaskAssign(Integer id);
	
	/**
	 * 根据taskManage的id获取集合
	 * @param taskManage
	 * @return
	 */
	List<TaskAssign> listTaskAssignManage(int taskManage);
	
	/**
	 * @param taskManage
	 * @param sucess
	 * @return
	 */
	List<TaskAssign> listTaskAssignManage(int taskManage,int success);
	
	/**
	 * @param urlHash
	 * @return
	 */
	TaskSite getSiteByHash(int taskRelation,String urlHash);
	
	/**根据URLHash 只获取 未分配和 正在配置的对象（StatusType.UrlSiteType）
	 * @param urlHash
	 * @return
	 */
	TaskSite getSiteByHashS(String urlHash);
	
	/**
	 * 根据用户id检索 排除  url重复的和url未分配的
	 * @param taskRelation
	 * @param userId
	 * @param name
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<TaskSite> listTaskSitePage(int taskRelation,int userId,String name,int pageNum, int pageSize);
	
	/**根据用户id检索 排除  url重复的和url未分配的
	 * @param taskRelation
	 * @param userId
	 * @param name
	 * @return
	 */
	List<TaskSite> listTaskSite(int taskRelation,int userId,String name);
	
	/**
	 * @param urlHash
	 * @return
	 */
	TaskSite getSiteByHash(String urlHash);
	
	
	/**查看当前用户分配任务
	 * @param userId
	 * @return
	 */
	Page<TaskAssign> pageTaskAssignUser(Integer userId,String manageName,CrawlerType manageType,LevelType manageLevelType,int success,int pageNum, int pageSize);

	/**
	 * 执行采集点统计
	 * @return
	 */
	public long countTaskSite ();

	/**
	 * 统计采集员完成的任务
	 */
	public List<Map<String, Object>> countTaskSuccessWithUser (String startTime, String endTime);

	/**
	 * 统计采集员未完成的任务
	 */
	public List<Map<String, Object>> countTaskUnSuccessWithUser (String startTime, String endTime);

	/**
	 * 根据任务状态进行统计
	 * @param statusType
	 * @return
	 */
	public long countByStatus (StatusType statusType);

}
