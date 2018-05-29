package com.bonc.uni.dcci.dao;

import com.bonc.uni.dcci.entity.TaskAssign;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 任务分配
 * @author futao
 * 2017年9月12日
 */
public interface TaskAssignRepository extends PagingAndSortingRepository<TaskAssign, Integer>, JpaSpecificationExecutor<TaskAssign>{

	
	@Transactional
    @Modifying
    @Query("DELETE FROM TaskAssign WHERE taskManage = ?1")
    int delete(int taskManage);

	//统计采集员完成的任务
    @Query(value =
            "SELECT  " +
                    "  COUNT(1), " +
                    "  ACCOUNT, " +
                    "  USERNAME " +
                    "FROM " +
                    "  (SELECT  " +
                    "    u.`account` AS ACCOUNT, " +
                    "    u.`user_name` AS USERNAME, " +
                    "    FROM_UNIXTIME( " +
                    "      ta.`LASTMODIFIEDTIME` / 1000, " +
                    "      '%Y-%m-%d' " +
                    "    ) AS ta_date  " +
                    "  FROM " +
                    "    dcci_task_assign ta  " +
                    "    INNER JOIN sys_user u  " +
                    "      ON ta.`USER_ID` = u.`id`  " +
                    "  GROUP BY ACCOUNT, " +
                    "    USERNAME, " +
                    "    ta.`SUCCESS`, " +
                    "    ta_date  " +
                    "  HAVING ta.`SUCCESS` = 2  " +
                    "    AND ta_date >= ?1  " +
                    "    AND ta_date <= ?2 ) AS a  " +
                    "GROUP BY a.ACCOUNT, " +
                    "  a.USERNAME  " +
                    "LIMIT 10 ",
            nativeQuery = true)
    public List<Object[]> countTaskSuccessWithUser (String startTime, String endTime);

    //统计采集员未完成的任务
    @Query(value =
            "SELECT  " +
                    "  COUNT(1), " +
                    "  ACCOUNT, " +
                    "  USERNAME  " +
                    "FROM " +
                    "  (SELECT  " +
                    "    u.`account` AS ACCOUNT, " +
                    "    u.`user_name` AS USERNAME, " +
                    "    FROM_UNIXTIME( " +
                    "      IF ( " +
                    "        ta.`LASTMODIFIEDTIME`, " +
                    "        ta.`LASTMODIFIEDTIME`, " +
                    "        ta.`CREATEDTIME` " +
                    "      ) / 1000, " +
                    "      '%Y-%m-%d' " +
                    "    ) AS ta_date  " +
                    "  FROM " +
                    "    dcci_task_assign ta  " +
                    "    INNER JOIN sys_user u  " +
                    "      ON ta.`USER_ID` = u.`id`  " +
                    "  GROUP BY ACCOUNT, " +
                    "    USERNAME, " +
                    "    ta.`SUCCESS`, " +
                    "    ta_date  " +
                    "  HAVING ta.`SUCCESS` <> 2  " +
                    "    AND ta_date >= ?1 " +
                    "    AND ta_date <= ?2 ) AS a  " +
                    "GROUP BY a.ACCOUNT, " +
                    "  a.USERNAME  " +
                    "LIMIT 10 ",
            nativeQuery = true)
    public List<Object[]> countTaskUnSuccessWithUser (String startTime, String endTime);

}
