package com.test.lock.core.bean;

import com.test.lock.cons.Constants;
import com.test.lock.core.manager.config.LockContext;
import lombok.Data;

/**
 * @Author heye
 * @Date 2020/1/10
 * @Decription
 */
@Data
public class LockKey {

    private String key;

    private Thread t;

    private volatile int state = Constants.DEFAULT_TRY_ACQUIRE;

    private String prefix;

    private String suffix;

    private LockKey(String key, String prefix, String suffix, Thread t) {
        this.key = key;
        this.prefix = prefix;
        this.suffix = suffix;
        this.t = t;
    }

    public boolean isExist() {
        return LockContext.keyMap.get(this.key) != null;
    }

    public boolean put() {
        return LockContext.keyMap.putIfAbsent(this.key, this) == null;
    }

    public boolean remove() {
        return LockContext.keyMap.remove(this.key) != null;
    }

    public boolean acquireIncr() {
        return acquireIncr(Constants.DEFAULT_TRY_ACQUIRE);
    }

    public boolean acquireIncr(int acquires) {
        Thread current = Thread.currentThread();
        if (current == getExclusiveOwnerThread()) {
            int nextc = state + acquires;
            if (nextc < 0) { // overflow
                throw new Error("Maximum lock count exceeded");
            }
            setState(nextc);
            return true;
        }
        return false;
    }

    public final boolean tryRelease() {
        return tryRelease(Constants.DEFAULT_TRY_ACQUIRE);
    }

    public final boolean tryRelease(int releases) {
        int c = getState() - releases;
        if (Thread.currentThread() != getExclusiveOwnerThread()) {
            throw new IllegalMonitorStateException();
        }
        boolean free = false;
        if (c == 0) {
            free = true;
            remove();
        }
        setState(c);
        return free;
    }

    private Thread getExclusiveOwnerThread() {
        return t;
    }

    public static LockKeyBuilder builder() {
        return new LockKeyBuilder();
    }

    public static class LockKeyBuilder {
        private String key;

        private String prefix;

        private String suffix;

        public LockKeyBuilder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public LockKeyBuilder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public LockKeyBuilder key(String key) {
            this.key = key;
            return this;
        }

        public LockKey build() {
            key = prefix == null ? "" : prefix + key + suffix == null ? "" : suffix;
            return new LockKey(key, prefix, suffix, Thread.currentThread());
        }
    }


}
