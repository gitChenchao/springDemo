package com.cc.spring.demo.spring.annotation;

import java.lang.annotation.*;

/**
 * 类名称：Service<br>
 * 类描述：<br>
 * 创建时间：2018年08月05日<br>
 *
 * @author 陈超
 * @version 1.0.0
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String value() default "";
}
