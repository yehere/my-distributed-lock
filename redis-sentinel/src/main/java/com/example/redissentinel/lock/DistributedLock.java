package com.example.redissentinel.lock;

import org.springframework.core.annotation.AliasFor;

import javax.validation.constraints.NotNull;
import java.lang.annotation.*;


/**
 * @Author heye
 * @Date 2020/1/4
 * @Decription
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLock {
    //key，expiretime，waittimeout，lockmanager,enable（指定全局时长等参数）等
    @NotNull
    @AliasFor("key")
    String value() default "";

    @AliasFor("value")
    String key() default "";

    String prefix() default "lock";

    long millis() default 1000;
}
