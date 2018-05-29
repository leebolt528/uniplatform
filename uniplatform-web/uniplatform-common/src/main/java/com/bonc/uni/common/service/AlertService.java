package com.bonc.uni.common.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.bonc.uni.common.entity.Alert;
import com.bonc.uni.common.util.PlatformType;

/**预警服务
 * @author futao
 * 2017年9月1日
 */
public interface AlertService {

	/**
	 * 添加
	 * @param alert
	 */
	void save(Alert alert);
	
	/**删除
	 * @param id
	 * @return
	 */
	boolean delById(Integer id);
	
	/**保存
	 * @param alerts
	 */
	void save(List<Alert> alerts);
	
	/**通过id得到对象
	 * @param id
	 * @return
	 */
	Alert getById(Integer id);
	
	/**列出所有
	 * @return
	 */
	List<Alert> listAll();
	
	/**列表翻页
	 * @param group
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<Alert> listByPage(PlatformType group,int pageNum, int pageSize);
	
	/**
	 * @param alert
	 */
	void saveAndSend(Alert alert);
	
	/**
	 * 列表
	 * @param infoId
	 * @param group
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<Alert> listByPage(int infoId,PlatformType group,int pageNum, int pageSize);
	
	/**
	 * 根据infoid删除
	 * @param infoId
	 * @return
	 */
	int delByInfo(int infoId);
}
