package com.bonc.uni.usou.service.clusters;

import com.bonc.uni.usou.config.UsouHttpConfiguration;
import com.bonc.uni.usou.dao.ClustersDao;
import com.bonc.uni.usou.entity.Clusters;
import com.bonc.uni.usou.entity.ClustersBuild;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.uni.usou.util.jsonParse.JsonParser;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yedunyao on 2017/8/10.
 */
@Service("clustersService")
public class ClustersServiceImpl implements ClustersService {

    @Autowired
    private ClustersDao clustersDao;
    @Autowired
    UsouHttpConfiguration usouHttpConfiguration;

    //默认第一页,分页长度为5
    public List<Clusters> findAll() {
        return findAll(0, 5, null, null);
    }

    //默认分页长度为5
    public List<Clusters> findAll(int page) {
        return findAll(page, 5, null, null);
    }

    public List<Clusters> findAll(int page, int size) {
        return findAll(page, size, null, null);
    }
    
    @Override
    public Clusters getById(Integer id) {
    	return clustersDao.findOne(id);
    }

    @Override
    public List<Clusters> findAll(int page, int size, String order, String properties) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");

        if(StringUtil.isNotEmpty(order)) {
            if(StringUtil.isEmpty(properties)) {
                properties = "id";
            }

            order = order.toLowerCase();
            switch (order) {
                case "asc":
                    sort = new Sort(Sort.Direction.ASC, properties);
                    break;
                case "desc":
                    sort = new Sort(Sort.Direction.DESC, properties);
                    break;
                default:
                    break;
            }
        }

        PageRequest pageRequest = new PageRequest(page, size, sort);
        Page<Clusters> all = null;
        List<Clusters> clustersList = null;
        try {
            all = clustersDao.findAll(pageRequest);
            clustersList = all.getContent();
        } catch (Exception e) {
            LogManager.error("clustersService findAll has exception :", e);
        }
        //List<ClustersBuild> clustersBuilds = parseClusters(clustersList);
        return getClustersHealth(clustersList);
    }

    @Override
    public List<Clusters> listAll() {
        return (List<Clusters>) clustersDao.findAll();
    }

    @Override
    public Clusters findClustersById(int id) {
        Clusters c = null;
        try {
            c = clustersDao.findById(id);
        } catch (Exception e) {
            LogManager.error("clustersService findById has exception :", e);
        }

        return c;
    }

    @Override
    public Clusters findById(int id) {
        Clusters c = null;
        try {
            c = clustersDao.findById(id);
        } catch (Exception e) {
            LogManager.error("clustersService findById has exception :", e);
        }

        return c;
    }

    @Override
    public Clusters findByConnectName(String connectName) {
        Clusters c = null;
        try {
            c = clustersDao.findByConnectName(connectName);
        } catch (Exception e) {
            LogManager.error("clustersService findByConnectName has exception :", e);
        }
        return c;
    }

    @Override
    public List<Clusters> findClusterByStateLessThan (int state, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        PageRequest pageRequest = new PageRequest(page, size, sort);
        Page<Clusters> all = null;
        try {
            all = clustersDao.findByStateLessThan(state, pageRequest);
        } catch (Exception e) {
            LogManager.error("clustersService findByStateLessThan has exception :", e);
        }
        return all.getContent();
    }

    /*@Override
    public List<ClustersBuild> findByStateLessThan (int state, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        PageRequest pageRequest = new PageRequest(page, size, sort);
        Page<Clusters> all = null;
        try {
            all = clustersDao.findByStateLessThan(state, pageRequest);
        } catch (Exception e) {
            LogManager.error("clustersService findByStateLessThan has exception :", e);
        }
        List<ClustersBuild> clustersBuilds = parseClusters(all.getContent());
        return clustersBuilds;
    }*/

    @Override
    public Clusters save(Clusters clusters) {
        Clusters c = null;
        try {
            c = clustersDao.save(clusters);
        } catch (Exception e) {
            LogManager.error("clustersService save has exception :", e);
        }
        return c;
    }


    @Override
    public int delete(int id) {

        try {
            return clustersDao.delete(id);
        } catch (Exception e) {
            LogManager.error("clustersService delete has exception :", e);
        }
        return -1;
    }

    @Override
    public int deleteByIds(String ids) {
        try {
            int[] arr = StringUtil.split2IntArray(ids);
            List<Integer> list = new ArrayList<>(arr.length);
            for(Integer id : arr) {
                list.add(id);
            }
            return clustersDao.deleteByIds(list);
        } catch (Exception e) {
            LogManager.error("clustersService deleteByIds has exception :", e);
        }
        return -1;
    }

    @Override
    public long count() {
        try {
            return clustersDao.count();
        } catch (Exception e) {
            LogManager.error("clustersService count has exception :", e);
        }
        return 0;
    }

    @Override
    public int updateClusterState(Clusters clusters) {
        try {
            return clustersDao.updateClusterState(clusters.getId(), clusters.getState());
        } catch (Exception e) {
            LogManager.error("clustersService update cluster state has exception :", e);
        }
        return -1;
    }

    public int updateClusterState(int id, int state) {
        try {
            return clustersDao.updateClusterState(id, state);
        } catch (Exception e) {
            LogManager.error("clustersService update cluster state has exception :", e);
        }
        return -1;
    }

     @Override
    public int updateCluster(Clusters clusters) {
         try {
             return clustersDao.updateCluster(clusters.getId(), clusters.getClusterName(),
                     clusters.getUri(), clusters.getConnectName(),
                     clusters.getUserName(), clusters.getPassword());
         } catch (Exception e) {
             LogManager.error("clustersService updateCluster has exception :", e);
         }
         return -1;
     }

    //get clusters health
    public List<Clusters> getClustersHealth(List<Clusters> clustersList) {

        //设置http请求超时时间为2s， 避免页面刷新慢
        RequestConfig requestConfig = usouHttpConfiguration.getShortRequestConfig();

        for (Clusters clusters : clustersList) {
            String uri = clusters.getUri();
            String user = clusters.getUserName();
            String password = clusters.getPassword();
            if (StringUtil.isEmpty(user)) {
                user = "";
            }
            if (StringUtil.isEmpty(password)) {
                password = "";
            }
            String authInfo = user + ":" + password;
            String[] urls = StringUtil.split(uri, ",");
            String url = null;
            for(int i = 0; i < urls.length; i++) {
                url = urls[i];
                String host = "http://" + url;
                String health = "";
                String status = "";
                try {
                    health = HttpRequest.doGet(requestConfig, host + "/_cluster/health", authInfo, "");
                    JsonParser healthParser = new JsonParser(health);
                    status = (String) healthParser.keys("status");
                } catch (Exception e) {
                    LogManager.Exception(e);
                }
                clusters.setHealth(status);
                if (!"".equals(status)) {
                    break;
                }
            }
        }
        return clustersList;
    }

    public ClustersBuild parseClusters(Clusters clusters) {
        ClustersBuild build = new ClustersBuild();
        build.setId(clusters.getId());
        build.setClusterName(clusters.getClusterName());
        build.setUri(clusters.getUri());
        build.setConnectName(clusters.getConnectName());
        //build.setUserName(clusters.getUserName());
        build.setHealth(clusters.getHealth());
        return build;
    }

    public List<ClustersBuild> parseClusters(List<Clusters> clustersList) {
        List<ClustersBuild> builds = new ArrayList<>(clustersList.size());

        for (Clusters clusters : clustersList) {
            ClustersBuild build = parseClusters(clusters);
            builds.add(build);
        }

        return builds;
    }

    public void tabUnConnectClusters () {
        List <Clusters> clustersList = listAll();
        //设置http请求超时时间为2s， 避免页面刷新慢
        RequestConfig requestConfig = usouHttpConfiguration.getShortRequestConfig();

        for (int j = 0; j < clustersList.size(); j++) {
            Clusters clusters = clustersList.get(j);
            int id = clusters.getId();
            String uri = clusters.getUri();
            String[] urls = StringUtil.split(uri, ",");
            String user = clusters.getUserName();
            String password = clusters.getPassword();
            if (StringUtil.isEmpty(user)) {
                user = "";
            }
            if (StringUtil.isEmpty(password)) {
                password = "";
            }
            String authInfo = user + ":" + password;
            String url = null;
            int state = 0;
            for(int i = 0; i < urls.length; i++) {
                url = urls[i];
                String host = "http://" + url;
                String health = "";
                String status = "";
                try {
                    health = HttpRequest.doGet(requestConfig, host + "/_cluster/health", authInfo, "");
                    JsonParser healthParser = new JsonParser(health);
                    status = (String) healthParser.keys("status");
                    switch (status) {
                        case "green":
                            state = 0;
                            break;
                        case "yellow":
                            state = 1;
                            break;
                        case "red":
                            state = 2;
                            break;
                    }
                    updateClusterState(id, state);
                    break;
                } catch (ResponseException e) {
                    state = 3;
                    updateClusterState(id, state);
                }
            }
        }
    }

}
