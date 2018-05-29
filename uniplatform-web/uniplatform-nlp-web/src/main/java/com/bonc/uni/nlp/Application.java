package com.bonc.uni.nlp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bonc.uni.common.quartz.service.SchedulerInit;
import com.bonc.uni.common.util.SpringContextUtil;
import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * Created by yedunyao on 2017/8/21.
 */

@ComponentScan("com.bonc.uni")
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableTransactionManagement
public class Application {
	
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

        ApplicationContext app = SpringApplication.run(Application.class, args);
        SpringContextUtil.setApplicationContext(app);
        SchedulerInit.InitScheduler();
        
        startSucessful();
    }


    private static void startSucessful() {
    	LogManager.Event("\n==================================\n"
	    		+ "************ BONC-NLAP ***********\n"
	    		+ "==================================\n"
	    		+ "Unitplatform-nlap startup success!\n"
	    		+ "==================================");
    }
}

