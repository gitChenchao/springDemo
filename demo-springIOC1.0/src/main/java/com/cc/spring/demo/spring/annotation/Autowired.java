package com.cc.spring.demo.spring.annotation;

import java.lang.annotation.*;

/**
 * 类名称：Autowired<br>
 * 类描述：<br>
 * 创建时间：2018年08月05日<br>
 *
 * @author 陈超
 * @version 1.0.0
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    String value() default "";
}
