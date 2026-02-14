package com.example.lifecyclelab;

import com.example.lifecyclelab.beans.LifecycleTarget;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final LifecycleTarget target;

    public Runner(LifecycleTarget target) {
        this.target = target;
    }

    @Override
    public void run(String... args) {
        System.out.println("Runner: bean class = " + target.getClass().getName()
                + ", isAopProxy=" + AopUtils.isAopProxy(target));

        target.doWork("hello");
    }
}
