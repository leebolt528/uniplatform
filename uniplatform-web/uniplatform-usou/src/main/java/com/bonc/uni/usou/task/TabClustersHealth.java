package com.bonc.uni.usou.task;

import com.bonc.uni.usou.service.clusters.ClustersService;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by yedunyao on 2017/10/17.
 */

@Component
@Configurable
@EnableScheduling
public class TabClustersHealth {

    @Autowired
    ClustersService clustersService;

    //每60秒执行一次，标记所有集群的健康状态信息
    @Scheduled(cron = "${usou.task.tabClusters.interval}")
    public void tabUnConnectClusters () {
        LogManager.method("Start to tab cluster health state.");
        clustersService.tabUnConnectClusters();
        LogManager.method("Finish to tab cluster health state.");
    }

}
