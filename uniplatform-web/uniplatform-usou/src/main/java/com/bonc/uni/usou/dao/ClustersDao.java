package com.bonc.uni.usou.dao;

import com.bonc.uni.usou.entity.Clusters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yedunyao on 2017/8/10.
 */
@Repository("clustersDao")
public interface ClustersDao extends CrudRepository<Clusters, Integer> {

    Iterable<Clusters> findAll();

    Page<Clusters> findAll(Pageable pageable);

    Clusters findById(int id);

    Clusters findByConnectName(String connectName);

    Page<Clusters> findByStateLessThan(int state, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Clusters WHERE id = ?1")
    int delete(int id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Clusters WHERE id in ?1")
    int deleteByIds(List<Integer> ids);

    @Transactional
    @Modifying
    @Query("UPDATE Clusters c SET c.clusterName = :clusterName, c.connectName= :connectName, " +
            "c.userName = :userName, c.password = :password, " +
            "c.uri = :uri WHERE c.id = :id")
    int updateCluster(@Param("id") int id, @Param("clusterName") String clusterName,
                      @Param("uri") String uri, @Param("connectName") String connectName,
                      @Param("userName") String userName, @Param("password") String password);

    @Transactional
    @Modifying
    @Query("UPDATE Clusters c SET c.state = :state WHERE c.id = :id")
    int updateClusterState(@Param("id") int id, @Param("state") int state);

}
