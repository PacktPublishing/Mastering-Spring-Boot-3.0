package com.packt.ahmeric.sandbox.aspect;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnExpression("${logging.aspect.enabled:false}")
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* com.packt.ahmeric..*.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Starting method: {}", joinPoint.getSignature().toShortString());

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        log.info("Completed method: {} in {} ms", joinPoint.getSignature().toShortString(), endTime - startTime);

        return result;
    }

    @Before("execution(* com.packt.ahmeric..*.*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        log.info("Entering method: {} with args {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()));
    }

    @After("execution(* com.packt.ahmeric..*.*(..))")
    public void logMethodExit(JoinPoint joinPoint) {
        log.info("Exiting method: {}", joinPoint.getSignature().toShortString());
    }
}

