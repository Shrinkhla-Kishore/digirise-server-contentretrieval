package com.digirise.contentretreival;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Date: 2020-03-07
 * Author: shrinkhlak
 */

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext s_context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        s_context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return s_context;
    }

    public static <T> T getBean(String name, Class<T> aClass) {
        return s_context.getBean(name, aClass);
    }

}
