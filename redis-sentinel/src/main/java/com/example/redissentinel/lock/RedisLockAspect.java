package com.example.redissentinel.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @Author heye
 * @Date 2020/1/4
 * @Decription
 */
@Aspect
@Component
public class RedisLockAspect {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private MyRedisLock myRedisLock;

    private Map<String, BeanDefinition> beans;

    @Pointcut("(@annotation(com.example.redissentinel.lock.DistributedLock))")
    public void lockPoint() {
        //do something
    }

    @Around("lockPoint()")
    public Object addLock(ProceedingJoinPoint pjp) {
        beans = beans == null ? context.getBeansOfType(BeanDefinition.class) : beans;
        String beanId = null;
        beans.entrySet().forEach(b->{
            if(b.getValue().equals(pjp.getThis())){
            }
        });
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        DistributedLock annotation = signature.getMethod().getAnnotation(DistributedLock.class);
        String prefix = annotation.prefix();
        String key = StringUtils.isEmpty(prefix) ? annotation.value() : prefix + annotation.value();
        long millis = annotation.millis();

        Object result = null;
        myRedisLock.lock(key, millis);
        try {
            try {
                result = pjp.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new Exception();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myRedisLock.unlock(key);
        }
        return result;
    }
}
