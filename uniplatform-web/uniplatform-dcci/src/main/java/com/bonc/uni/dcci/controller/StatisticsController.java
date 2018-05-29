package com.bonc.uni.dcci.controller;

import com.bonc.uni.common.util.ResultUtil;
import com.bonc.uni.dcci.service.*;
import com.bonc.uni.dcci.util.StatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yedunyao on 2017/9/25.
 * 管控首页
 */
@RestController
@RequestMapping("/dcci/statistics")
public class StatisticsController {

    @Autowired
    ServerService serverService;
    @Autowired
    CrawlerService crawlerService;
    @Autowired
    SiteService siteService;
    @Autowired
    TaskManageService taskManageService;
    @Autowired
    DataApiService dataApiService;

    @RequestMapping("")
    public String statistics () {

        Map<String, Long> statisticsMap = new HashMap <>(10);

        //总采集点
        long site_count = siteService.count();
        //执行采集点
        long exec_site_count = taskManageService.countTaskSite();
        //服务器
        long server_count = serverService.countServer();
        //采集器
        long crawler_count = crawlerService.count();
        //未分配任务
        long unassign_task_count = taskManageService.countByStatus(StatusType.UNDISTRIBUTED);
        //执行任务
        long exec_task_count = taskManageService.countByStatus(StatusType.EXECUTE);;
        //api
        long api_count = dataApiService.countDataApi();

        //TODO: 没有数据，暂时写死
        //采集量
        long craw_amount_count = 200;
        //入库量
        long save_count = 200;
        //处理量
        long handle_count = 200;

        statisticsMap.put("site_count", site_count);
        statisticsMap.put("exec_site_count", exec_site_count);
        statisticsMap.put("server_count", server_count);
        statisticsMap.put("crawler_count", crawler_count);
        statisticsMap.put("unassign_task_count", unassign_task_count);
        statisticsMap.put("exec_task_count", exec_task_count);
        statisticsMap.put("api_count", api_count);
        statisticsMap.put("craw_amount_count", craw_amount_count);
        statisticsMap.put("save_count", save_count);
        statisticsMap.put("handle_count", handle_count);

        return ResultUtil.success("请求成功", statisticsMap);
    }

    /**
     * 统计机器中采集器数量
     * @return
     */
    @RequestMapping("/count/crawler")
    public String countCrawlerByServerIp () {
        List <Map <String, Object>> mapList = crawlerService.countCrawlerByServerIp();
        return ResultUtil.success("请求成功", mapList);
    }

    /**
     * 统计采集器中站点数量
     * @return
     */
    @RequestMapping("/count/site")
    public String countSiteByCrawlerName () {
        List <Map <String, Object>> mapList = siteService.countSiteByCrawlerName();
        return ResultUtil.success("请求成功", mapList);
    }

    /**
     * 按日期统计总采集点
     * @Param time 格式"YYYY-MM-DD"
     */
    @RequestMapping("/count/site/createdTime")
    public String countSiteByCreatedTime (String startTime, String endTime) {
        List <Map <String, Object>> mapList = siteService.countSiteByCreatedTime(startTime, endTime);
        return ResultUtil.success("请求成功", mapList);
    }


    /**
     * 采集员完成任务
     */
    @RequestMapping("/count/task/success")
    public String countTaskSuccessWithUser (String startTime, String endTime) {
        List <Map <String, Object>> mapList = taskManageService.countTaskSuccessWithUser(startTime, endTime);
        return ResultUtil.success("请求成功", mapList);
    }

    /**
     * 采集员未完成任务
     */
    @RequestMapping("/count/task/unsuccess")
    public String countTaskUnSuccessWithUser (String startTime, String endTime) {
        List <Map <String, Object>> mapList = taskManageService.countTaskUnSuccessWithUser(startTime, endTime);
        return ResultUtil.success("请求成功", mapList);
    }

}
