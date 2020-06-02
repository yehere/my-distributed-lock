package com.test.lock.aspect;

import com.test.lock.core.annotation.DistributedLock;
import com.test.lock.core.manager.LockManager;
import com.test.lock.core.manager.config.TempConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @Author heye
 * @Date 2020/1/9
 * @Decription
 */
@Aspect
@Component
public class DistributedLockAspect {
    public Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

//    private Map<String, BeanDefinition> beans;

    @Pointcut("(@annotation(com.test.lock.core.annotation.DistributedLock))")
    public void lockPoint() {
        //do something
    }

    @Around("lockPoint()")
    public Object addLock(ProceedingJoinPoint pjp) {
        String beanId = null;
        LockManager lockManager = null;

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        DistributedLock annotation = signature.getMethod().getAnnotation(DistributedLock.class);

        //获取注解的所有参数
        String key = annotation.key();
        String prefix = annotation.prefix() == null ? "" : annotation.prefix();
        String managerName = annotation.lockManager();
        boolean enableFilter = annotation.enableFilter();
        long exprMillis = annotation.exprMillis();
        long waitMillis = annotation.waitMillis();

        //获取配置的lockmanager
        Map<String, LockManager> lockManagers = context.getBeansOfType(LockManager.class);
        if (null == lockManagers) {
            logger.error("no impl of lockmanager found！@DistributedLock disabled");
            throw new Error("no impl of lockmanager found！@DistributedLock disabled");
        }
        if (!StringUtils.isEmpty(managerName)) {
            lockManager = lockManagers.get(managerName);
        } else {
            lockManager = new ArrayList<>(lockManagers.values()).get(0);
        }
        if (null == lockManager) {
            logger.error("no impl of lockmanager found！@DistributedLock disabled");
            throw new Error("no impl of lockmanager found！@DistributedLock disabled");
        }

        //获取分布式锁的key
        Object[] args = pjp.getArgs();
        key = StringUtils.isEmpty(prefix) ? key : (prefix + key);
        if (StringUtils.isEmpty(key)) {
            Map<String, ?> beans = context.getBeansOfType(pjp.getThis().getClass());
//            beans = CollectionUtils.isEmpty(beans) ? context.getBeansOfType(pjp.getThis().getClass()) : beans;
            Iterator<? extends Map.Entry<String, ?>> it = beans.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, ?> next = it.next();
                if (next.getValue().equals(pjp.getThis())) {
                    beanId = next.getKey();
                    break;
                }
            }
            String methodName = signature.getMethod().getName();
            StringBuilder builder = new StringBuilder("");
            for (Object arg : args) {
                builder.append(arg.getClass().getName());
            }
            key = beanId + methodName + builder.toString();
        }

        //业务逻辑的分布式锁控制
        Object result = null;
        TempConfig tempConfig = new TempConfig().toBuilder()
                .prefix(!StringUtils.isEmpty(prefix) ? prefix : null)
                .enableFilter(enableFilter)
                .enableStatis(lockManager.getConf().enableStatis)
                .expireMillis(exprMillis > 0 ? exprMillis : null)
                .waitMillis(waitMillis > 0 ? waitMillis : null)
                .build();
        Lock lock = lockManager.getLock(key, tempConfig);
        try {
            lock.lock();//阻塞获取分布式锁
            result = pjp.proceed();//执行业务逻辑
            return result;
        } catch (Exception e) {
            logger.error("{}执行异常:{}", pjp.getTarget().getClass().getTypeName(), e);
        } catch (Throwable throwable) {
            logger.error("{}执行异常:{}", pjp.getTarget().getClass().getTypeName(), throwable);
        } finally {
            lock.unlock();
        }
        return result;
    }
}
