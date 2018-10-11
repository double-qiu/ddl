package com.ddl.basic.config;


import org.springframework.context.ApplicationContext;

/**
 *  spring 容器上下文，用过获取单例bean
 * @author DOUBLE
 */
public abstract class ApplicationContextHolder {
    private volatile static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }


    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }


    public synchronized static void setApplicationContext(ApplicationContext appContext) {
        if (applicationContext == null) {
            applicationContext = appContext;
        }
    }
}
