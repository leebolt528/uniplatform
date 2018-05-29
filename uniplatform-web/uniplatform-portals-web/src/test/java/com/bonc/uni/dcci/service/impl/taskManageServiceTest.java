package com.bonc.uni.dcci.service.impl;

import com.bonc.uni.common.entity.user.SysUser;
import com.bonc.uni.dcci.dao.TaskAssignRepository;
import com.bonc.uni.dcci.entity.TaskAssign;
import com.bonc.uni.dcci.entity.TaskManage;
import com.bonc.uni.dcci.service.TaskManageService;
import com.bonc.uni.dcci.util.CrawlerType;
import com.bonc.uni.dcci.util.LevelType;
import com.bonc.uni.dcci.util.StatusType;
import com.bonc.uni.portals.PortalsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.*;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalsApplication.class)
public class taskManageServiceTest {

	@Autowired
	TaskManageService taskManageService;

	@Autowired
	TaskAssignRepository taskAssignRepository;

	@Test
	public void testFindTaskAssign() {
		try{
		String account = "test";
		LevelType levelType = LevelType.COMMONLY;
		CrawlerType type = CrawlerType.DOMESTIC_NEWS;
		Specification<TaskAssign> crawlerSpecification = new Specification<TaskAssign>() {
			public Predicate toPredicate(Root<TaskAssign> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<TaskAssign, SysUser> join = root.join("sysUser", JoinType.LEFT);
				Join<TaskAssign, TaskManage> join2 = root.join("manage", JoinType.LEFT);
				Predicate accoutQuery = cb.like(join.get("account"), "%" + account + "%");
				Predicate levelQuery = cb.equal(join2.<LevelType>get("levelType"), levelType);
				Predicate typeQuery = cb.equal(join2.<CrawlerType>get("type"), type);
				Predicate nameQuery = cb.equal(root.<Integer>get("crawlerId"), 2);
				return cb.and(nameQuery,accoutQuery, levelQuery,typeQuery);

				// return null;
			}
		};
		List<TaskAssign> lists = taskAssignRepository.findAll(crawlerSpecification);
		System.out.println(lists.size());
		if(lists.size()>0) {
			System.out.println(lists.get(0).getSysUser().getAccount());
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		TaskAssign taskAssign = new TaskAssign();
		taskAssign.setUserId(7);
		//taskAssign.setUserName("张三");
		taskAssign.setTaskManage(2);
		taskAssign.setDeadline(1111111111l);
		taskAssign.setSiteAssign(11);
		taskAssign.setCrawlerId(2);

		/*taskAssign.setManageLevelType(LevelType.COMMONLY);
		taskAssign.setManageName("www");
		taskAssign.setManageType(CrawlerType.ABROAD_BLOG);
		taskAssign.setTaskRelation(1);*/
		taskAssignRepository.save(taskAssign);
	}

	@Test
	public void testCountByStatus () {
		long countByStatus = taskManageService.countByStatus(StatusType.UNDISTRIBUTED);
		System.out.println(countByStatus);
	}
}
