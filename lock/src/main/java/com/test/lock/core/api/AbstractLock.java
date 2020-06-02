package com.test.lock.core.api;

import com.test.lock.core.manager.config.BaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;

/**
 * @Author heye
 * @Date 2020/1/13
 * @Decription
 *         任务通过竞争获取锁才能对该资源进行操作(①竞争锁)；
 *         当有一个任务在对资源进行更新时（②占有锁），
 *         其他任务都不可以对这个资源进行操作（③任务阻塞），
 *         直到该任务完成更新(④释放锁)
 */
public abstract class AbstractLock<T extends BaseConfiguration> implements Lock {
    public Logger logger = LoggerFactory.getLogger(this.getClass());

    public String key;//锁的key

    public String lockType;//锁类型

    public T conf;//配置文件

    public AbstractLock(){
    }

    public AbstractLock(AbstractLock lock){
        this.lockType = lock.lockType;
        this.key = lock.key;
        this.conf = (T)lock.conf;//可以去掉
    }

    @Override
    public final void lock(){
        //尝试获得锁资源
        //①竞争锁
        if (tryLock()) {
            logger.info("##成功获取lock锁的资源####");
        } else {
            //③任务阻塞
            waitLock();

            //重新获取锁资源
            lock();
        }
        //②占有锁
    }

    public abstract void waitLock();
}
