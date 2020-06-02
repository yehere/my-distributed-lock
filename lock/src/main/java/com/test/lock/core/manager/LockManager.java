package com.test.lock.core.manager;

import com.test.lock.core.ILockManager;
import com.test.lock.core.api.AbstractLock;
import com.test.lock.core.api.wapper.DistributedLockFilterWapper;
import com.test.lock.core.api.wapper.DistributedLockStatisWapper;
import com.test.lock.core.api.wapper.ReentrantDistributedLockWapper;
import com.test.lock.core.manager.config.BaseConfiguration;
import com.test.lock.core.manager.config.TempConfig;

import java.util.concurrent.locks.Lock;

/**
 * @Author heye
 * @Date 2020/1/10
 * @Decription
 */
public abstract class LockManager<T extends BaseConfiguration> implements ILockManager {
    public T conf;

    /**
     * 默认参数的获取锁
     * @param key
     * @return
     */
    public abstract Lock getLock(String key);

    /**
     * 自定义参数的获取锁
     * @param key
     * @param tempConfig
     * @return
     */
    public abstract Lock getLock(String key, TempConfig tempConfig);

    public AbstractLock wapperReentrant(AbstractLock lock){
        lock = new ReentrantDistributedLockWapper(lock);
        if(conf.enableFilter){
            lock = new DistributedLockFilterWapper(lock);
        }
        if(conf.enableStatis){
            lock = new DistributedLockStatisWapper(lock);
        }
        return lock;
    }

    public AbstractLock wapperReentrant(AbstractLock lock, TempConfig tempConfig){
        lock = new ReentrantDistributedLockWapper(lock);
        if(tempConfig.getEnableFilter()){
            lock = new DistributedLockFilterWapper(lock);
        }
        if(tempConfig.getEnableStatis()){
            lock = new DistributedLockStatisWapper(lock);
        }
        return lock;
    }

    public T getConf() {
        return conf;
    }

    public void setConf(T conf) {
        this.conf = conf;
    }
}
