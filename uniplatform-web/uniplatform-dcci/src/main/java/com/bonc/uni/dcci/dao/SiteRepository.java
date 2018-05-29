package com.bonc.uni.dcci.dao;

import com.bonc.uni.dcci.entity.Site;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 站点dao
 * @author futao
 * 2017年9月5日
 */
public interface SiteRepository extends PagingAndSortingRepository<Site, Integer>, JpaSpecificationExecutor<Site>{

	@Transactional
    @Modifying
    @Query("DELETE FROM Site WHERE crawlerId = ?1")
    int delete(int crawlerId);

    @Query(value =
            "SELECT  " +
                    "  COUNT(s.`CRAWLER_ID`), " +
                    "  c.`IP`, " +
                    "  c.`PORT`, " +
                    "  c.`NAME`  " +
                    "FROM " +
                    "  dcci_site s  " +
                    "  INNER JOIN dcci_crawler c  " +
                    "    ON s.`CRAWLER_ID` = c.`ID`  " +
                    "GROUP BY s.`CRAWLER_ID`, " +
                    "  c.`IP`, " +
                    "  c.`PORT`, " +
                    "  c.`NAME`  " +
                    "ORDER BY c.`IP`, " +
                    "  c.`PORT`, " +
                    "  c.`NAME`, " +
                    "  s.`CRAWLER_ID`  " +
                    "LIMIT 10",
            nativeQuery = true)
    public List<Object[]> countSiteByCrawlerName ();
    
    @Query(value = 
            "SELECT  " +
                    "  COUNT(s.CREATEDTIME), " +
                    "  FROM_UNIXTIME(s.CREATEDTIME / 1000, '%Y-%m-%d') AS \"CRAWLER_DATE\"  " +
                    "FROM " +
                    "  dcci_site s  " +
                    "GROUP BY CRAWLER_DATE  " +
                    "HAVING CRAWLER_DATE >= ?1 " +
                    "  AND CRAWLER_DATE <= ?2 " +
                    "LIMIT 10 "
            , nativeQuery = true)
    public List<Object[]> countSiteByCreatedTime (String startTime, String endTime);
    
}
