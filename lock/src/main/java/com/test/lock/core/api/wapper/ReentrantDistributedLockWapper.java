package com.test.lock.core.api.wapper;


import com.test.lock.core.api.AbstractLock;
import com.test.lock.core.bean.LockKey;
import com.test.lock.core.manager.config.LockContext;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @Author heye
 * @Date 2020/1/14
 * @Decription
 */
public class ReentrantDistributedLockWapper extends AbstractLock {
    private AbstractLock lock;

    private LockKey lockKey;

    public ReentrantDistributedLockWapper(AbstractLock lock) {
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
        lockKey = LockContext.keyMap.get(lock.key);
        //1.尝试加锁
        boolean result = ((lockKey != null && lockKey.acquireIncr())
                || lock.tryLock());
        if (result) {
            if(lockKey==null){
                lockKey = LockKey.builder().key(key).build();
            }
            lockKey.put();
        }
        return result;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return lock.tryLock(time,unit);
    }

    @Override
    public void unlock() {
        if(lockKey.tryRelease()){
            lock.unlock();
        }
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }
}
