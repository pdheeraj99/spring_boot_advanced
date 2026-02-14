package com.example.lifecyclelab.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class DemoBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("BFPP: postProcessBeanFactory (before beans are instantiated)");

        BeanDefinition bd = beanFactory.getBeanDefinition("initMethodBean");
        MutablePropertyValues pvs = bd.getPropertyValues();
        pvs.add("message", "set by BeanFactoryPostProcessor");
    }
}
