package com.mars.mars_uam.annotation;

import java.lang.annotation.*;

/**
 * 系统日志排除注解 <br/>
 * 可以注解方法
 * 
 * Created by wuketao on 2017/7/8.
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SystemLogExcludeAnnotation {

}
