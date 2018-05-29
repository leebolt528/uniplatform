package com.bonc.uni.usou.service.clusters;

import com.bonc.uni.usou.entity.Clusters;

import java.util.List;

/**
 * Created by yedunyao on 2017/8/10.
 */
public interface ClustersService {

    public List<Clusters> findAll();

    public List<Clusters> findAll(int page);

    public List<Clusters> findAll(int page, int size);

    public List<Clusters> findAll(int page, int size, String order, String properties);

    public List<Clusters> listAll();

    public Clusters findClustersById(int id);

    public Clusters findById(int id);

    public Clusters findByConnectName(String connectName);

    public List<Clusters> findClusterByStateLessThan (int state, int page, int size);

    /*public List<ClustersBuild> findByStateLessThan (int state, int page, int size);*/

    public Clusters save(Clusters cluster);

    public int delete(int id);

    public int deleteByIds(String ids);

    public long count();

    public int updateCluster(Clusters clusters);

    public int updateClusterState(int id, int state);

    public int updateClusterState(Clusters clusters);
    
    /**通过id获取
     * @param id
     * @return
     */
    Clusters getById(Integer id);

    /**
     * 查询所有的集群，并标记集群的状态
     */
    public void tabUnConnectClusters ();

}
