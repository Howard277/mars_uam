package com.mars.mars_uam.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解 <br/>
 * 可以注解类、方法
 * 
 * Created by wuketao on 2017/7/8.
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SystemLogAnnotation {

}
