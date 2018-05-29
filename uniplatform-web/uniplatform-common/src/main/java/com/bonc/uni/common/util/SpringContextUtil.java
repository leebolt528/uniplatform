package com.bonc.uni.common.util;

import org.springframework.context.ApplicationContext;


/**
 * 获取上下文
 * @author futao
 * 2017年8月30日
 */
public class SpringContextUtil {
    private static ApplicationContext applicationContext;

    //获取上下文
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //设置上下文
    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    //通过名字获取上下文中的bean
    @SuppressWarnings("unchecked")
	public static <T> T getBean(String name){
        return (T) applicationContext.getBean(name);
    }

    //通过类型获取上下文中的bean
    public static Object getBean(Class<?> requiredType){
        return applicationContext.getBean(requiredType);
    }
}
