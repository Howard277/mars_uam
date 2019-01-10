package com.mars.mars_uam.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Arrays;

/**
 * 定义一个日志切片类，用来记录重要方法的调用情况。、
 * Created by wuketao on 2017/7/8.
 */
@Slf4j
@Aspect
@Configuration
public class SystemLogConfig {
    /*
     * 系统日志 类 切入点
     */
    @Pointcut("@within(com.mars.mars_uam.annotation.SystemLogAnnotation)")
    public void systemLogClass() {
        // 无需处理
    }

    /*
     * 系统日志 方法 切入点
     */
    @Pointcut("@annotation(com.mars.mars_uam.annotation.SystemLogAnnotation)")
    public void systemLogMethod() {
        // 无需处理
    }

    /*
     * 系统日志 排除方法 切入点
     */
    @Pointcut("@annotation(com.mars.mars_uam.annotation.SystemLogExcludeAnnotation)")
    public void systemLogExclude() {
        // 无需处理
    }

    /**
     * 系统日志切入点<br/>
     * 对于使用“系统日志类、方法注解，并且没有使用排除方法注解的方法生效”
     */
    @Pointcut("(systemLogClass()||systemLogMethod())&&(!systemLogExclude())")
    public void systemLog() {
        // 无需处理
    }

    /*
     * 前置通知
     */
    @Before("systemLog()")
    public void doBefore(JoinPoint jp) {
        Object[] os = jp.getArgs();// 获得入参
        String className = jp.getTarget().getClass().getSimpleName();// 获得类名称
        String methodName = jp.getSignature().getName();// 获得方法名称
        log.info("方法名：{} 入参：{}", className + "." + methodName, Arrays.toString(os));
    }

    /*
     * 后置通知
     */
    @After("systemLog()")
    public void doAfter(JoinPoint jp) {
        // 无需处理
    }

    /*
     * 环绕通知
     */
    @Around("systemLog()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getTarget().getClass().getSimpleName();// 获得类名称
        String methodName = className + "." + pjp.getSignature().getName();// 获得方法名称
        Instant startTime = Instant.now();// 记录程序的开始时间
        Object retVal = pjp.proceed();// 执行目标方法，并获得返回值。
        Instant endTime = Instant.now();// 记录程序的结束时间
        int spendTime = (endTime.getNano() - startTime.getNano()) / 1000000;//用时(ms)
        log.info("方法名：{} 用时：{}", methodName, spendTime);
        return retVal;
    }

    /*
     * 异常返回通知
     */
    // @AfterThrowing(pointcut = "systemLog()", throwing = "ex")
    public void doThrowing(JoinPoint jp, Throwable ex) {
        String className = jp.getTarget().getClass().getSimpleName();// 获得类名称
        String methodName = className + "." + jp.getSignature().getName();// 获得方法名称
        log.info("方法名：{} 异常信息：{}", methodName, ex);
    }
}
