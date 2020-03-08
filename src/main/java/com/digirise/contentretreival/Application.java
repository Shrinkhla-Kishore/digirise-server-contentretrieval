package com.digirise.contentretreival;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication (scanBasePackages = {"com.digirise.contentretreival"})
public class Application {
    private static final Logger s_logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        String[] beans = context.getBeanDefinitionNames();
        s_logger.info("Beans in the application is of length {} ", beans.length);
        for (int i=0; i<beans.length; i++) {
            s_logger.info("{}", beans[i]);
        }
    }

}
