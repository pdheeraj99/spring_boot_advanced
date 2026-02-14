package com.example.lifecyclelab.lifecycle;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor, Ordered {

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private boolean watch(String beanName) {
        return beanName.equals("lifecycleTarget") || beanName.equals("initMethodBean");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (watch(beanName)) {
            System.out.println("BPP: BEFORE init  beanName=" + beanName + " type=" + bean.getClass().getName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (watch(beanName)) {
            System.out.println("BPP: AFTER  init  beanName=" + beanName
                    + " type=" + bean.getClass().getName()
                    + " isAopProxy=" + AopUtils.isAopProxy(bean));
        }
        return bean;
    }
}
