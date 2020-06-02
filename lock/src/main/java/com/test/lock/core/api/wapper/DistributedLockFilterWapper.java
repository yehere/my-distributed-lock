package com.test.lock.core.api.wapper;


import com.test.lock.core.api.AbstractLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @Author heye
 * @Date 2020/1/14
 * @Decription
 */
public class DistributedLockFilterWapper extends AbstractLock {
    private AbstractLock lock;

    public DistributedLockFilterWapper(AbstractLock lock) {
        super(lock);
        this.lock = lock;
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
        synchronized (lock.key) {
            return lock.tryLock();
        }
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
