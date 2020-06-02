package com.test.lock.core.api.wapper;


import com.test.lock.core.api.AbstractLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @Author heye
 * @Date 2020/1/14
 * @Decription
 */
public class DistributedLockStatisWapper extends AbstractLock {
    private AbstractLock lock;

    private long startTime;

    public DistributedLockStatisWapper(AbstractLock lock) {
        super(lock);
        this.lock = lock;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void waitLock() {
        lock.waitLock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        boolean result = lock.tryLock();
        if(result){
            logger.info("==本次锁({})竞争耗时：{}ms==",lock.lockType,System.currentTimeMillis()- startTime);
        }
        return result;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return lock.tryLock(time,unit);
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }
}
