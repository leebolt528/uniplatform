package com.bonc.uni.dcci.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bonc.uni.dcci.entity.ServerPwd;

/**
 * 服务器的非root用户
 * @author futao
 * 2017年8月28日
 */
@Transactional
public interface ServerPwdRepository extends PagingAndSortingRepository<ServerPwd, Integer>, JpaSpecificationExecutor<ServerPwd>{

}
