package com.jcm.statistics.data;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jcm.statistics.Util;

public class DataServer {

    public static void main(String[] args) {
        try {
            Util.initResource();
            startDataAccessorService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startDataAccessorService(){
        /*ApplicationContext getBean lazy-init = false lazy load*/
        ApplicationContext context = new ClassPathXmlApplicationContext("conf/appcontextrmiserver.xml");
        // 加载Bean即启动logPersonService
        context.getBean("dataAccessorService");
    }
}
