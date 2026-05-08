package com.example.stormGuard.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class StormGuardLoggingAspect {


    @Around("execution(* com.example.stormGuard.controllers.stormGuardController.*(..))")
    public Object logAroundMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info(">>> Before execution: {} | Args: {}", methodName, args);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        log.info("<<< After execution: {} | Duration: {}ms", methodName, duration);

        return result;
    }
}
