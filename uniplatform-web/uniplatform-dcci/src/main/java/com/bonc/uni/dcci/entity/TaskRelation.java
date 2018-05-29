package com.bonc.uni.dcci.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.bonc.uni.common.entity.EntityCommon;

/**
 * TaskManage 和 TaskSite 关联
 * @author futao
 * 2017年9月12日
 */
@Entity
@Table(name = "`dcci_task_relation`")
public class TaskRelation extends EntityCommon{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
