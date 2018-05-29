package com.bonc.uni.dcci.dao;

import com.bonc.uni.dcci.entity.Server;
import com.bonc.uni.dcci.entity.ServerModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 服务器的Repository
 * @author futao
 * 2017年8月28日
 */
@Transactional
public interface ServerRepository extends PagingAndSortingRepository<Server, Integer>, JpaSpecificationExecutor<Server>{

    @Query(value = "select new com.bonc.uni.dcci.entity.ServerModel(s.id, s.ip) from Server s")
    public List<ServerModel> findAllServerModel();

    public List<Server> findByIp(String ip);

}
