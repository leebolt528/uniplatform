package com.bonc.uni.dcci.service.impl;

import com.bonc.uni.common.jpa.Specification.SpecificationUtil;
import com.bonc.uni.common.util.MapUtil;
import com.bonc.uni.dcci.dao.TaskAssignRepository;
import com.bonc.uni.dcci.dao.TaskManageRepository;
import com.bonc.uni.dcci.dao.TaskRelationRepository;
import com.bonc.uni.dcci.dao.TaskSiteRepository;
import com.bonc.uni.dcci.entity.TaskAssign;
import com.bonc.uni.dcci.entity.TaskManage;
import com.bonc.uni.dcci.entity.TaskRelation;
import com.bonc.uni.dcci.entity.TaskSite;
import com.bonc.uni.dcci.service.TaskManageService;
import com.bonc.uni.dcci.util.CrawlerType;
import com.bonc.uni.dcci.util.LevelType;
import com.bonc.uni.dcci.util.StatusType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service("taskManageService")
public class TaskManageServiceImpl implements TaskManageService {

	@Autowired
	TaskManageRepository taskManageRepository;
	
	@Autowired
	TaskRelationRepository taskRelationRepository;
	
	@Autowired
	TaskSiteRepository taskSiteRepository;
	
	@Autowired
	TaskAssignRepository taskAssignRepository;
	
	@PersistenceContext
    private EntityManager em;
	
	@Override
	public boolean save(TaskManage taskManage) {
		try {
			taskManageRepository.save(taskManage);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public TaskManage saveTaskManage(TaskManage taskManage) {
		return taskManageRepository.save(taskManage);
	}
	
	@Override
	public TaskAssign saveTaskAssign(TaskAssign taskAssign) {
		return taskAssignRepository.save(taskAssign);
	}
	
	@Override
	public TaskRelation saveTaskRelation(TaskRelation taskRelation) {
		return taskRelationRepository.save(taskRelation);
	}
	
	@Override
	public boolean delTaskRelationById(Integer id) {
		try {
			taskRelationRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public List<TaskSite> savesTaskSite(List<TaskSite> taskSites) {
		return (List<TaskSite>) taskSiteRepository.save(taskSites);
	}
	
	@Override
	public TaskSite getSiteByHash(int taskRelation,String urlHash) {
		Specification<TaskSite> spec = SpecificationUtil.<TaskSite>and().ep("taskRelation", taskRelation).ep("urlHash", urlHash).build();
		return taskSiteRepository.findOne(spec);
	}
	
	@Override
	public TaskSite getSiteByHash(String urlHash) {
		Specification<TaskSite> spec = SpecificationUtil.<TaskSite>and().ep("urlHash", urlHash).build();
		return taskSiteRepository.findOne(spec);
	}
	
	@Override
	public TaskSite getSiteByHashS(String urlHash) {
		Specification<TaskSite> spec = SpecificationUtil.<TaskSite>and().ep("urlHash", urlHash).ep("status", StatusType.UrlSiteType.CONFIGURING,StatusType.UrlSiteType.UNDISTRIBUTED).build();
		return taskSiteRepository.findOne(spec);
	}
	
	@Override
	@Transactional
	public boolean updsTaskSite(List<TaskSite> taskSites) {
		try {
			for(int i = 0; i < taskSites.size(); i++) { 
				em.merge(taskSites.get(i));
				if(i%30==0)
	            {
	                em.flush();
	                em.clear();

	            }
			}
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public List<TaskAssign> savesTaskAssign(List<TaskAssign> taskAssigns){
		return (List<TaskAssign>) taskAssignRepository.save(taskAssigns);
	}
	
	@Override
	public boolean delTaskSite(List<TaskSite> taskSites) {
		try {
			taskSiteRepository.delete(taskSites);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public int delTaskSite(int taskRelation) {
		return taskSiteRepository.delete(taskRelation);
	}
	
	@Override
	public int updateSiteRecall(int taskRelation, int userId,StatusType.UrlSiteType status) {
		return taskSiteRepository.updateRecall(taskRelation, userId, status);
	}
	
	@Override
	public int delTaskAssign(int taskManage) {
		return taskAssignRepository.delete(taskManage);
	}
	
	@Override
	public List<TaskSite> listTaskSite(int taskRelation,String name,StatusType.UrlSiteType status){
		Specification<TaskSite> spec = SpecificationUtil.<TaskSite>and().ep("taskRelation", taskRelation).ep(status != null, "status", status)
				.like(StringUtils.isNotBlank(name),"name", "%"+name+"%").build();
		return taskSiteRepository.findAll(spec);
	}
	
	@Override
	public List<TaskSite> listTaskSite(int taskRelation,int userId,String name){
		Specification<TaskSite> spec = SpecificationUtil.<TaskSite>and().ep("taskRelation", taskRelation)
				.ep("userId", userId).ep("status", StatusType.UrlSiteType.COMPLETE,StatusType.UrlSiteType.CONFIGURING,StatusType.UrlSiteType.FAIL)
				.like(StringUtils.isNotBlank(name),"name", "%"+name+"%").build();
		return taskSiteRepository.findAll(spec);
	}
	
	@Override
	public boolean update(TaskManage taskManage) {
		try {
			
			TaskManage upd = getById(taskManage.getId());
			if(null == upd ) {
				return false;
			}
			upd.setName(taskManage.getName());
			upd.setDeadline(taskManage.getDeadline());
			upd.setLevelType(taskManage.getLevelType());
			upd.setDescription(taskManage.getDescription());
			upd.setSiteTotal(taskManage.getSiteTotal());
			upd.setSiteRepeat(taskManage.getSiteRepeat());
			upd.setStatus(taskManage.getStatus());
			upd.setAssignType(taskManage.getAssignType());
			taskManageRepository.save(upd);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean updTaskAssign(TaskAssign taskAssign) {
		try {
			TaskAssign upd = getTaskAssign(taskAssign.getId());
			if(null == upd ) {
				return false;
			}
			upd.setDeadline(taskAssign.getDeadline());
			upd.setSiteAssign(taskAssign.getSiteAssign());
			upd.setSiteComplete(taskAssign.getSiteComplete());
			upd.setSiteFail(taskAssign.getSiteFail());
			upd.setSuccess(taskAssign.getSuccess());
			upd.setCheck(taskAssign.getCheck());
			/*upd.setManageLevelType(taskAssign.getManageLevelType());
			upd.setManageName(taskAssign.getManageName());
			upd.setManageType(taskAssign.getManageType());*/
			taskAssignRepository.save(upd);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public TaskManage getById(Integer id) {
		return taskManageRepository.findOne(id);
	}
	
	@Override
	public TaskAssign getTaskAssign(Integer id) {
		return taskAssignRepository.findOne(id);
	}
	
	@Override
	public boolean delById(Integer id) {
		try {
			taskManageRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public TaskManage getByRelation(int taskRelation) {
		Specification<TaskManage> spec = SpecificationUtil.<TaskManage>and().ep("taskRelation", taskRelation).build();
		return taskManageRepository.findOne(spec);
	}
	
	@Override
	public Page<TaskManage> list(String name,StatusType status,CrawlerType crawlerType,int pageNum, int pageSize){
		Specification<TaskManage> spec = SpecificationUtil.<TaskManage>and().
				like(StringUtils.isNotBlank(name),"name", "%"+name+"%").
				ep(status != null, "status", status).
				ep(crawlerType != null, "type", crawlerType).orderByDESC("createdTime").build();
		return taskManageRepository.findAll(spec, new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public Page<TaskManage> list(String name,StatusType status,Object[] ids ,int pageNum, int pageSize){
		Specification<TaskManage> spec = SpecificationUtil.<TaskManage>and().ep("id", ids).
				like(StringUtils.isNotBlank(name),"name", "%"+name+"%").
				ep(status!= null, "status", status).build();
		return taskManageRepository.findAll(spec, new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public Page<TaskSite> listTaskSitePage(int taskRelation,String name,StatusType.UrlSiteType status,int pageNum, int pageSize){
		Specification<TaskSite> spec = SpecificationUtil.<TaskSite>and().ep("taskRelation", taskRelation).
				ep(status != null, "status", status)
				.like(StringUtils.isNotBlank(name),"name", "%"+name+"%").build();
		return taskSiteRepository.findAll(spec, new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public Page<TaskSite> listTaskSitePage(int taskRelation,int userId,String name,int pageNum, int pageSize){
		Specification<TaskSite> spec = SpecificationUtil.<TaskSite>and().ep("taskRelation", taskRelation)
				.ep("userId", userId).ep("status", StatusType.UrlSiteType.COMPLETE,StatusType.UrlSiteType.CONFIGURING,StatusType.UrlSiteType.FAIL)
				.like(StringUtils.isNotBlank(name),"name", "%"+name+"%").build();
		return taskSiteRepository.findAll(spec, new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public List<TaskAssign> listTaskAssignUser(int userId){
		Specification<TaskAssign> spec = SpecificationUtil.<TaskAssign>and().ep( "userId", userId).build();
		return taskAssignRepository.findAll(spec);
	}
	
	@Override
	public TaskAssign getTaskAssignUser(int userId,int taskManage){
		Specification<TaskAssign> spec = SpecificationUtil.<TaskAssign>and().ep( "userId", userId).ep("taskManage", taskManage).build();
		return taskAssignRepository.findOne(spec);
	}
	
	@Override
	public Page<TaskAssign> listTaskAssign(int taskManage,int pageNum, int pageSize){
		Specification<TaskAssign> spec = SpecificationUtil.<TaskAssign>and().ep("taskManage", taskManage).orderByDESC("createdTime").build();
		return taskAssignRepository.findAll(spec,  new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public List<TaskAssign> listTaskAssignManage(int taskManage){
		Specification<TaskAssign> spec = SpecificationUtil.<TaskAssign>and().ep( "taskManage", taskManage).build();
		return taskAssignRepository.findAll(spec);
	}
	
	@Override
	public Page<TaskAssign> pageTaskAssignUser(Integer userId,String manageName,CrawlerType manageType,LevelType manageLevelType,int success,int pageNum, int pageSize){
		/*Specification<TaskAssign> spec = SpecificationUtil.<TaskAssign>and().ep( "userId", userId)
				.ep(manageLevelType != null, "manageLevelType", manageLevelType)
				.ep(manageType != null, "manageType", manageType)
				.like(StringUtils.isNotBlank(manageName),"manageName", "%"+manageName+"%").build();*/
		Specification<TaskAssign> spec = new Specification<TaskAssign>() {
			public Predicate toPredicate(Root<TaskAssign> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> lists = new LinkedList<Predicate>();
				Join<TaskAssign, TaskManage> typejoin = root.join("manage", JoinType.LEFT);
				Predicate userIdQuery = cb.equal(root.<Integer>get("userId"), userId);
				lists.add(userIdQuery);
				if(StringUtils.isNotBlank(manageName)) {
					//Predicate manageNameQuery = cb.like(root.<String>get("manageName"), "%"+manageName+"%");
					Predicate manageNameQuery = cb.like(typejoin.<String>get("name"), "%"+manageName+"%");
					lists.add(manageNameQuery);
				}
				if(manageType != null) {
					Predicate typeQuery = cb.equal(typejoin.<CrawlerType>get("type"), manageType);
					lists.add(typeQuery);
				}
				if(manageLevelType != null) {
					Predicate levelQuery = cb.equal(typejoin.<LevelType>get("levelType"), manageLevelType);
					lists.add(levelQuery);
				}
				if(success >= 0) {
					Predicate successQuery = cb.equal(root.<Integer>get("success"), success);
					lists.add(successQuery);
				}
				query.orderBy(cb.desc(root.get("createdTime")));
				Predicate[] predicates = new Predicate[lists.size()];
				return cb.and(lists.toArray(predicates));
			}
		};
		return taskAssignRepository.findAll(spec,new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public List<TaskAssign> listTaskAssignManage(int taskManage,int success){
		Specification<TaskAssign> spec = SpecificationUtil.<TaskAssign>and().ep( "taskManage", taskManage).ep("success", success).build();
		return taskAssignRepository.findAll(spec);
	}

	@Override
	public long countTaskSite () {
		return taskSiteRepository.count();
	}

	//统计采集员完成的任务
	@Override
	public List<Map<String, Object>> countTaskSuccessWithUser (String startTime, String endTime) {
		List <Object[]> objects = taskAssignRepository.countTaskSuccessWithUser(startTime, endTime);
		List<Map<String, Object>> mapList = new ArrayList<>(objects.size());
		objects.forEach( (Object[] objArr) -> {
			long task_success_count = ((BigInteger) objArr[0]).longValue();
			String account = (String) objArr[1];
			String userName = (String) objArr[2];
			Map<String, Object> map = MapUtil.newMap()
					.put("account", account)
					.put("userName", userName)
					.put("task_success_count", task_success_count)
					.build();
			mapList.add(map);
		});
		return mapList;
	}

	//统计采集员未完成的任务
	@Override
	public List<Map<String, Object>> countTaskUnSuccessWithUser (String startTime, String endTime) {
		List <Object[]> objects = taskAssignRepository.countTaskUnSuccessWithUser(startTime, endTime);
		List<Map<String, Object>> mapList = new ArrayList <>(objects.size());
		objects.forEach( (Object[] objArr) -> {
			long task_unsuccess_count = ((BigInteger) objArr[0]).longValue();
			String account = (String) objArr[1];
			String userName = (String) objArr[2];
			Map<String, Object> map = MapUtil.newMap()
					.put("account", account)
					.put("userName", userName)
					.put("task_unsuccess_count", task_unsuccess_count)
					.build();
			mapList.add(map);
		});
		return mapList;
	}

	/**
	 * 根据任务状态进行统计
	 * @param statusType
	 * @return
	 */
	@Override
	public long countByStatus (StatusType statusType) {
		return taskManageRepository.countByStatus(statusType);
	}

}
