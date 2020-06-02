package com.test.lock.core.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Author heye
 * @Date 2020/1/9
 * @Decription
 * 默认值见 BaseConfiguration及其实现类，这里最好不要设置默认值会造成全局覆盖,如需单次覆盖设置可在使用注解的时候指定相应属性
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLock {
    @AliasFor("lockManager")
    String value() default "";

    @AliasFor("value")
    String lockManager() default "";

    String key() default "";

    //不建议修改
    boolean enableFilter() default true;

    //default "lock"
    String prefix() default "";

    //default 10
    long waitMillis() default 0;

    /**
     * 锁的最大持有时长
     * default 1000 * 10
     * @return
     */
    long exprMillis() default 0;

    //超时策略。。。

    //key的生成策略

}
