package com.example.lifecyclelab.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TracingAspect {

    @Around("execution(* com.example.lifecyclelab.beans.LifecycleTarget.doWork(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("ASPECT: before " + pjp.getSignature());
        Object out = pjp.proceed();
        System.out.println("ASPECT: after  " + pjp.getSignature());
        return out;
    }
}
