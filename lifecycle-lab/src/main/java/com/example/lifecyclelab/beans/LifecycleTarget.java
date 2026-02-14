package com.example.lifecyclelab.beans;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class LifecycleTarget implements
        BeanNameAware, ApplicationContextAware, InitializingBean, DisposableBean {

    private HelperBean helper;

    public LifecycleTarget() {
        System.out.println("LifecycleTarget: constructor");
    }

    @Autowired
    public void setHelper(HelperBean helper) {
        this.helper = helper;
        System.out.println("LifecycleTarget: setter DI (setHelper)");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("LifecycleTarget: BeanNameAware.setBeanName = " + name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        System.out.println("LifecycleTarget: ApplicationContextAware.setApplicationContext");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("LifecycleTarget: @PostConstruct");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("LifecycleTarget: InitializingBean.afterPropertiesSet()");
    }

    public String doWork(String input) {
        System.out.println("LifecycleTarget: doWork(" + input + ")");
        return "ok";
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("LifecycleTarget: @PreDestroy");
    }

    @Override
    public void destroy() {
        System.out.println("LifecycleTarget: DisposableBean.destroy()");
    }
}
