package com.bonc.uni.dcci.dao;

import com.bonc.uni.dcci.entity.Crawler;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


/**
 * 采集器 jpa
 * @author futao
 * 2017年9月6日
 */
public interface CrawlerRepository extends PagingAndSortingRepository<Crawler, Integer>, JpaSpecificationExecutor<Crawler>{

    @Query(value =
            "SELECT  " +
                    "  COUNT(c.`IP`), " +
                    "  c.`IP`  " +
                    "FROM " +
                    "  dcci_crawler c  " +
                    "GROUP BY c.`IP`  " +
                    "LIMIT 10 ",
            nativeQuery = true)
    public List<Object[]> countCrawlerByServerIp ();

    public List<Crawler> findByServerId(int serverId);

    public List<Crawler> findCrawlerByServerIdAndPort(int serverId, String port);

}
