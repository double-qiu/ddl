package com.ddl.basic;


import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

//mybatis-generator:generate
@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
@EnableScheduling
@MapperScan("com.ddl.basic.dao")
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @SuppressWarnings("unused")
	private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        try {
            SpringApplication.run(Application.class, args);
            logger.info("#####  程序启动 -  SUCCESS  #####");
        } catch (Exception e) {
            logger.info("#####  程序启动失败  #####", e);
            System.exit(0);
        }
    }
}
