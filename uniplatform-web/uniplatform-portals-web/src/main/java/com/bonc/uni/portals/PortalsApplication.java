package com.bonc.uni.portals;

import com.bonc.uni.common.quartz.service.SchedulerInit;
import com.bonc.uni.common.util.PathUtil;
import com.bonc.uni.common.util.SpringContextUtil;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by yedunyao on 2017/8/21.
 */
@ComponentScan("com.bonc.uni")
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableTransactionManagement
public class PortalsApplication {

    static {
        try {
            //配置LogManager
            String logManagerPath = PathUtil.getConfigPath();
            LogManager.init(new Log4jTraceParameters("uniplatform", logManagerPath));
        } catch (Exception e) {
            System.out.println("Init logManager failed.");
        }
    }

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(PortalsApplication.class, args);
        SpringContextUtil.setApplicationContext(app);
        SchedulerInit.InitScheduler();
    }


}

