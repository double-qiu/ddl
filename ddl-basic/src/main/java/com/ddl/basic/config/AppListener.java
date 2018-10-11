package com.ddl.basic.config;


import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

@Configuration
public class AppListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextClosedEvent) {
            if (event.getSource() instanceof AnnotationConfigApplicationContext) {
//                ComputeRunner.stop();
            }
        } else if (event instanceof ApplicationReadyEvent) {
//            ComputeRunner.start();
        }
    }
}
