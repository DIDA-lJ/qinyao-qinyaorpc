package com.qinyao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LinQi
 * @createTime 2023-08-21
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TryTimes {
    
    int tryTimes() default 3;
    int intervalTime() default 2000;
    
}
